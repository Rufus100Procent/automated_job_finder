package aj.finder.ajfinder.service.ai;

import aj.finder.ajfinder.api.external.ApiClient;
import aj.finder.ajfinder.dto.platsbanken.JobHitDto;
import aj.finder.ajfinder.dto.ai.AiEvaluation;
import aj.finder.ajfinder.modal.ReleventJob;
import aj.finder.ajfinder.repository.ReleventJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AiEvaluationService extends ApiClient {

    @Value("${ollama.model}")
    private String ollamaModel;

    @Value("${cv.upload-dir}")
    private String uploadDir;

    @Value("${ai.timeout:120}")
    private long aiTimeout;

    private final ReleventJobRepository releventJobRepository;

    public AiEvaluationService(@Qualifier("ollamaWebClient") WebClient webClient,
                               ReleventJobRepository releventJobRepository) {
        super(webClient);
        this.releventJobRepository = releventJobRepository;
    }

    public Mono<ReleventJob> evaluateJobAndSave(JobHitDto jobHit) {
        File cvFile = getCvFile().orElse(null);
        if (cvFile == null) {
            log.error("No CV file found in directory: {}", uploadDir);
            return Mono.error(new RuntimeException("No CV file found."));
        }
        log.info("Processing CV file: {}", cvFile.getName());
        String extractedText = extractTextFromPdf(cvFile.getAbsolutePath());
        if (extractedText.isBlank()) {
            log.error("Extracted CV text is empty from file: {}", cvFile.getName());
            return Mono.error(new RuntimeException("CV text extraction failed or the file is empty."));
        }
        Map<String, Object> requestBody = createRequestBody(jobHit.getDescription().getText(), extractedText);
        log.info("Sending request to DeepSeek for job '{}' with headline: {}", jobHit.getHeadline(), jobHit.getHeadline());

        return postForFlux("/api/generate", requestBody, AiEvaluation.class)
//                .doOnNext(chunk -> log.info("Chunk received: {}", chunk.getResponse()))
                .collectList()
                .map(chunks -> {
                    String combinedResponse = chunks.stream()
                            .map(AiEvaluation::getResponse)
                            .collect(Collectors.joining())
                            .trim();
                    AiEvaluation finalEvaluation = new AiEvaluation();
                    finalEvaluation.setResponse(combinedResponse);
                    log.info("AI evaluation response: {}", combinedResponse);
                    return finalEvaluation;
                })
                .flatMap(aiEvaluation -> {
                    String response = aiEvaluation.getResponse().trim();
                    String cleanedResponse = response.replaceAll("(?is)<think>.*?</think>", "").trim();
                    Matcher matcher = getDecisionPattern().matcher(cleanedResponse);
                    if (matcher.find()) {
                        String decision = matcher.group(1).trim();
                        String explanation = matcher.group(2).trim();
                        String coverLetter = matcher.group(3) != null ? matcher.group(3).trim() : "";
                        log.info("Extracted decision: {}", decision);
                        log.info("Extracted explanation: {}", explanation);
                        log.info("Extracted cover letter: {}", coverLetter);

                        ReleventJob job = new ReleventJob(
                                jobHit.getWebpage_url(),
                                jobHit.getHeadline(),
                                explanation,
                                coverLetter
                        );
                        if (decision.equalsIgnoreCase("Yes")) {
                            log.info("Candidate qualified. Saving job: {}", jobHit.getWebpage_url());
                            return releventJobRepository.save(job);
                        } else {
                            log.info("Candidate not qualified. Returning result without saving.");
                            return Mono.just(job);
                        }
                    } else {
                        log.info("Could not parse AI response for job {}. Response: {}", jobHit.getId(), cleanedResponse);
                        ReleventJob defaultJob = new ReleventJob(
                                jobHit.getWebpage_url(),
                                jobHit.getHeadline(),
                                "Not qualified: " + cleanedResponse,
                                ""
                        );
                        return Mono.just(defaultJob);
                    }
                })
                .doOnError(error -> log.error("Error during AI evaluation", error))
                .timeout(Duration.ofSeconds(aiTimeout));
    }

    private Optional<File> getCvFile() {
        File dir = new File(uploadDir);
        if (!dir.exists() || !dir.isDirectory()) {
            log.error("CV upload directory does not exist: {}", uploadDir);
            return Optional.empty();
        }
        return Arrays.stream(Optional.ofNullable(dir.listFiles((d, name) -> name.toLowerCase().endsWith(".pdf")))
                        .orElse(new File[0]))
                .findFirst();
    }

    private Map<String, Object> createRequestBody(String jobDescription, String extractedText) {
        String fullPrompt = "CV Content:\n" + extractedText + "\n\nJob Description:\n" + jobDescription +
                "\n\nAnalyze the CV compared to the job description. " +
                "If the candidate qualifies based solely on the CV, respond with:\n" +
                "Yes\n{{Explanation}}: [brief explanation].\n{{CoverLetter}}: [brief, personalized cover letter].\n" +
                "If the candidate does NOT qualify, respond with:\n" +
                "No\n{{Explanation}}: [brief explanation stating why they do not qualify].\n" +
                "Do not output any placeholder text or internal thinking.";
        return Map.of(
                "model", ollamaModel,
                "system", "You are an AI assistant that analyzes CVs and job descriptions. " +
                        "Decide if the candidate qualifies based solely on the CV. " +
                        "Output a simple decision: 'Yes' or 'No'. " +
                        "If 'Yes', include '{{Explanation}}:' and '{{CoverLetter}}:' with brief texts. " +
                        "If 'No', output only '{{Explanation}}:' explaining why and do not include a cover letter. " +
                        "Do not output any placeholder text or internal thinking.",
                "prompt", fullPrompt,
                "temperature", 0.2
        );
    }

    private String extractTextFromPdf(String filePath) {
        try (PDDocument document = Loader.loadPDF(new File(filePath))) {
            String extractedText = new PDFTextStripper().getText(document).trim();
            return extractedText.isEmpty() ? "" : extractedText;
        } catch (IOException e) {
            log.error("Error extracting text from PDF: {}", e.getMessage());
            return "";
        }
    }

    private Pattern getDecisionPattern() {
        return Pattern.compile("(?i)^(Yes|No)\\s*\\{\\{Explanation\\}\\}:\\s*(.+?)(?:\\{\\{CoverLetter\\}\\}:\\s*(.+))?$", Pattern.DOTALL);
    }
}
