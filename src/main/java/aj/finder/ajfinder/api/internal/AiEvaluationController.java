package aj.finder.ajfinder.api.internal;

import aj.finder.ajfinder.dto.ai.AiEvaluation;
import aj.finder.ajfinder.service.ai.AiEvaluationService;
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
        return aiEvaluationService.assessJobQualificationUsingCv(prompt);
    }

}

