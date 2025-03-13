package aj.finder.ajfinder.api;

import aj.finder.ajfinder.modal.ai.AiEvaluation;
import aj.finder.ajfinder.service.AiEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiEvaluationController {

    private final AiEvaluationService aiEvaluationService;

    @PostMapping("/evaluate")
    public Mono<AiEvaluation> evaluate(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        return aiEvaluationService.evaluatePrompt(prompt);
    }

}

