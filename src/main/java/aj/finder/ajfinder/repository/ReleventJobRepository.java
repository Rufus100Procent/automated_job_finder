package aj.finder.ajfinder.repository;


import aj.finder.ajfinder.modal.ReleventJob;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ReleventJobRepository extends ReactiveMongoRepository<ReleventJob, String> {
    Mono<Boolean> existsByWebpageUrl(String webpageUrl);
}

