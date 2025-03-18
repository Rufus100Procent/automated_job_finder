package aj.finder.ajfinder.api.internal;


import aj.finder.ajfinder.modal.platsbanken.JobAd;
import aj.finder.ajfinder.modal.platsbanken.SearchResponse;
import aj.finder.ajfinder.service.job.JobSearchService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/jobs")
public class JobSearchController {

    private final JobSearchService jobSearchService;

    public JobSearchController(JobSearchService jobSearchService) {
        this.jobSearchService = jobSearchService;
    }

    @GetMapping("/search")
    public Mono<SearchResponse> searchJobs(@RequestParam String q,
                                           @RequestParam(required = false) Boolean remote) {
        return jobSearchService.search(q, remote);
    }

    @GetMapping("/{id}")
    public Mono<JobAd> getJob(@PathVariable String id) {
        return jobSearchService.getJobById(id);
    }

}

