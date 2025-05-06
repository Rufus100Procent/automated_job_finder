package aj.finder.ajfinder.service.job;

import aj.finder.ajfinder.api.external.ApiClient;
import aj.finder.ajfinder.dto.platsbanken.DescriptionDto;
import aj.finder.ajfinder.dto.platsbanken.JobHitDto;
import aj.finder.ajfinder.dto.platsbanken.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

// source https://data.arbetsformedlingen.se/data/jobsearch
// source https://jobsearch.api.jobtechdev.se/


@Service
@Slf4j
public class JobSearchService extends ApiClient {

    public JobSearchService(@Qualifier("jobSearchWebClient") WebClient webClient) {
        super(webClient);
    }

    public Mono<List<JobHitDto>> search(String query, Boolean remote, List<String> regions, String publishedAfter) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/search")
                            .queryParam("q", query);
                    if (remote != null) {
                        uriBuilder.queryParam("remote", remote);
                    }
                    if (regions != null && !regions.isEmpty()) {
                        for (String region : regions) {
                            uriBuilder.queryParam("region", region);
                        }
                    }
                    if (publishedAfter != null && !publishedAfter.isEmpty()) {
                        uriBuilder.queryParam("published-after", publishedAfter);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(SearchResponse.class)
                .doOnNext(response -> log.info("Total results found: {}", response.getTotal().getValue()))
                .map(response -> {
                    int total = response.getTotal().getValue();
                    return response.getHits().stream()
                            .map(hit -> new JobHitDto(
                                    hit.getId(),
                                    total,
                                    hit.getWebpage_url().toString(),
                                    hit.getHeadline(),
                                    new DescriptionDto(hit.getDescription().getText())
                            ))
                            .collect(Collectors.toList());
                });
    }
}



