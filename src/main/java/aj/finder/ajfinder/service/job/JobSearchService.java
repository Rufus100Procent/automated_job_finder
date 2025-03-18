package aj.finder.ajfinder.service.job;

import aj.finder.ajfinder.api.external.ApiClient;
import aj.finder.ajfinder.modal.platsbanken.JobAd;
import aj.finder.ajfinder.modal.platsbanken.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

// source https://data.arbetsformedlingen.se/data/jobsearch
// source https://jobsearch.api.jobtechdev.se/

@Service
@Slf4j
public class JobSearchService extends ApiClient {

    public JobSearchService(@Qualifier("jobSearchWebClient") WebClient webClient) {
        super(webClient);
    }

    public Mono<SearchResponse> search(String query, Boolean remote) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/search")
                            .queryParam("q", query);
                    if(remote != null) {
                        uriBuilder.queryParam("remote", remote);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(SearchResponse.class);
    }

    public Mono<JobAd> getJobById(String id) {
        return get("/ad/" + id, JobAd.class);
    }
}


