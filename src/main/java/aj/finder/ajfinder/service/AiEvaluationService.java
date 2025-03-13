package aj.finder.ajfinder.service;

import aj.finder.ajfinder.modal.ai.AiEvaluation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AiEvaluationService {

    private final WebClient webClient;

    @Value("${ollama.server.url}")
    private String ollamaUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    public AiEvaluationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<AiEvaluation> evaluatePrompt(String userPrompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", ollamaModel);
        requestBody.put("system", "You are an AI assistant.");
        requestBody.put("prompt", userPrompt);
        requestBody.put("max_new_tokens", 256);
        requestBody.put("temperature", 0.7);
        log.info("Request body: {}", requestBody);

        return webClient.post()
                .uri(ollamaUrl + "/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(AiEvaluation.class)
                .doOnNext(chunk -> log.info("Chunk received: {}", chunk.getResponse()))
                .collectList()
                .map(chunks -> {
                    StringBuilder sb = new StringBuilder();
                    for (AiEvaluation chunk : chunks) {
                        sb.append(chunk.getResponse());
                    }
                    AiEvaluation finalResponse = new AiEvaluation();
                    finalResponse.setResponse(sb.toString().trim());
                    return finalResponse;
                })
                .doOnError(error -> log.error("Error during evaluation", error))
                .timeout(Duration.ofSeconds(120));
    }

}





