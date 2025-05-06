package aj.finder.ajfinder.api.internal;

import aj.finder.ajfinder.dto.platsbanken.JobHitDto;
import aj.finder.ajfinder.modal.ReleventJob;
import aj.finder.ajfinder.service.ai.AiEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiEvaluationController {

    private final AiEvaluationService aiEvaluationService;

    @PostMapping("/evaluate")
    public Mono<ResponseEntity<ReleventJob>> evaluateJob(@RequestBody JobHitDto jobHitDto) {
        return aiEvaluationService.evaluateJobAndSave(jobHitDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


}

