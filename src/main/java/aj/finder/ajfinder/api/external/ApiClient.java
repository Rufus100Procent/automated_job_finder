package aj.finder.ajfinder.api.external;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class ApiClient {

    public final WebClient webClient;

    public ApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public  <T, R> Mono<R> post(String uri, T body, Class<R> responseType) {
        return webClient.post()
                .uri(uri)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType);
    }

    public  <T> Flux<T> postForFlux(String uri, Object body, Class<T> responseType) {
        return webClient.post()
                .uri(uri)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(responseType);
    }

    public  <T> Mono<T> get(String uri, Class<T> responseType) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType);
    }
}
