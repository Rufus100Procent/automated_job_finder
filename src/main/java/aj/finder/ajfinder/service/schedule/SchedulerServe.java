package aj.finder.ajfinder.service.schedule;

import aj.finder.ajfinder.repository.ReleventJobRepository;
import aj.finder.ajfinder.service.ai.AiEvaluationService;
import aj.finder.ajfinder.service.job.JobSearchService;
import aj.finder.ajfinder.service.mail.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class SchedulerServe implements InitializingBean {

    private final JobSearchService jobSearchService;
    private final AiEvaluationService aiEvaluationService;
    private final EmailService emailService;
    private final ReleventJobRepository releventJobRepository;

    private static final String PUBLISHED_AFTER = "2025-04-04T00:00:00";
    private static final String EMAIL_TO = "asa@duck.com";
    private final String[] JOB_TITLE = {
            "DevOps Engineer", "Java Developer", "Platform Engineer Java", "Spring Boot Developer"
    };

    public SchedulerServe(JobSearchService jobSearchService,
                          AiEvaluationService aiEvaluationService,
                          EmailService emailService,
                          ReleventJobRepository releventJobRepository) {
        this.jobSearchService = jobSearchService;
        this.aiEvaluationService = aiEvaluationService;
        this.emailService = emailService;
        this.releventJobRepository = releventJobRepository;
    }

    @Override
    public void afterPropertiesSet() {
        // Phase 1: Remote search with remote=true for all job titles.
        Mono<Void> remoteSearch = Flux.fromArray(JOB_TITLE)
                .concatMap(jobTitle -> processJobTitle(jobTitle, true, null))
                .then();

        // Phase 2: Region search with remote=null and specified regions.
        Mono<Void> regionSearch = Flux.fromArray(JOB_TITLE)
                .concatMap(jobTitle -> processJobTitle(jobTitle, null, List.of("G6DV_fKE_Viz", "CifL_Rzy_Mku")))
                .then();

        // Chain both phases and then send email if there are new jobs.
        remoteSearch
                .then(regionSearch)
                .then(emailService.sendAllRelevantJobsEmail(EMAIL_TO))
                .block();

        log.info("Completed processing all job titles and email sent.");
    }

    private Mono<Void> processJobTitle(String jobTitle, Boolean remote, List<String> regions) {
        log.info("Processing job title: {} with remote={} and regions={}", jobTitle, remote, regions);
        return jobSearchService.search(jobTitle, remote, regions, PUBLISHED_AFTER)
                .flatMap(jobs -> {
                    if (jobs.isEmpty()) {
                        log.info("No jobs found for title: {}", jobTitle);
                        return Mono.empty();
                    }
                    log.info("Found {} jobs for title: {}", jobs.size(), jobTitle);
                    return Flux.fromIterable(jobs)
                            // Skip if the job's URL already exists in MongoDB.
                            .filterWhen(jobHit ->
                                    releventJobRepository.existsByWebpageUrl(jobHit.getWebpage_url())
                                            .map(exists -> !exists)
                            )
                            // Process sequentially to limit concurrent AI evaluations.
                            .concatMap(jobHit -> {
                                log.info("Sending job {} for AI evaluation...", jobHit.getId());
                                return aiEvaluationService.evaluateJobAndSave(jobHit)
                                        .doOnNext(result -> log.info("Job {} processed -> AI Result: {}", jobHit.getId(), result));
                            })
                            .then();
                });
    }
}