package aj.finder.ajfinder.service;

import aj.finder.ajfinder.AbstractApiClient;
import aj.finder.ajfinder.modal.ai.AiEvaluation;
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
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class AiEvaluationService extends AbstractApiClient {

    @Value("${ollama.model}")
    private String ollamaModel;

    @Value("${cv.upload-dir}")
    private String uploadDir;

    public AiEvaluationService(@Qualifier("ollamaWebClient") WebClient webClient) {
        super(webClient);
    }


    public Mono<AiEvaluation> evaluatePromptWithLatestCv(String userPrompt) {
        File latestCvFile = getLatestCvFile().orElse(null);

        if (latestCvFile == null) {
            log.error("No CV file found in directory: {}", uploadDir);
            return Mono.error(new RuntimeException("No CV file found."));
        }

        log.info("Processing CV file: {}", latestCvFile.getName());

        String extractedText = extractTextFromPdf(latestCvFile.getAbsolutePath());

        if (extractedText.isBlank()) {
            log.error("Extracted CV text is empty from file: {}", latestCvFile.getName());
            return Mono.error(new RuntimeException("CV text extraction failed or the file is empty."));
        }

        Map<String, Object> requestBody = createRequestBody(userPrompt, extractedText);

        log.info("Sending request to DeepSeek with extracted CV text...");

        return postForFlux("/api/generate", requestBody, AiEvaluation.class)
                .doOnNext(chunk -> log.info("Chunk received: {}", chunk.getResponse()))
                .collectList()
                .map(chunks -> {
                    String combinedResponse = chunks.stream()
                            .map(AiEvaluation::getResponse)
                            .collect(Collectors.joining())
                            .trim();

                    AiEvaluation finalEvaluation = new AiEvaluation();
                    finalEvaluation.setResponse(combinedResponse);
                    return finalEvaluation;
                })
                .doOnError(error -> log.error("Error during AI evaluation", error))
                .timeout(Duration.ofSeconds(120));
    }

    private Map<String, Object> createRequestBody(String userPrompt, String extractedText) {
        String fullPrompt = "CV Content:\n" + extractedText + "\n\nUser Prompt:\n" + userPrompt +
                "\n\nYour response must start with 'Yes' or 'No' as the first word, followed by a short and clear explanation in one or two sentences.";

        return Map.of(
                "model", ollamaModel,
                "system", "You are an AI assistant that analyzes CVs and job descriptions to determine" +
                        " if the person qualifies for the job. Your response must always start with" +
                        " 'Yes' or 'No' as the first word, followed by a short and clear explanation in one or two sentences.",
                "prompt", fullPrompt,
                "temperature", 0.2
        );
    }

    private Optional<File> getLatestCvFile() {
        File dir = new File(uploadDir);

        if (!dir.exists() || !dir.isDirectory()) {
            log.error("CV upload directory does not exist: {}", uploadDir);
            return Optional.empty();
        }

        return Arrays.stream(Optional.ofNullable(dir.listFiles((d, name) -> name.toLowerCase().endsWith(".pdf")))
                        .orElse(new File[0]))
                .max(Comparator.comparingLong(File::lastModified));
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
}




