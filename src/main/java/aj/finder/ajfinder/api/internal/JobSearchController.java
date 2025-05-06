package aj.finder.ajfinder.api.internal;


import aj.finder.ajfinder.dto.platsbanken.JobHitDto;
import aj.finder.ajfinder.modal.ReleventJob;
import aj.finder.ajfinder.service.ReleventJobService;
import aj.finder.ajfinder.service.job.JobSearchService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobSearchController {

    private final JobSearchService jobSearchService;
    private final ReleventJobService releventJobService;

    public JobSearchController(JobSearchService jobSearchService, ReleventJobService releventJobService) {
        this.jobSearchService = jobSearchService;
        this.releventJobService = releventJobService;
    }

    @GetMapping("/search")
    public Mono<List<JobHitDto>> searchJobs(@RequestParam String q,
                                            @RequestParam(required = false) Boolean remote,
                                            @RequestParam(required = false) List<String> region,
                                            @RequestParam(required = false) String publishedAfter) {
        return jobSearchService.search(q, remote, region, publishedAfter);
    }

    @GetMapping
    public Flux<ReleventJob> getAllReleventJobs() {
        return releventJobService.getAllJobs();
    }

}

