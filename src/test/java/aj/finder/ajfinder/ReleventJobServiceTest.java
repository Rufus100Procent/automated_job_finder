package aj.finder.ajfinder;

import aj.finder.ajfinder.modal.ReleventJob;
import aj.finder.ajfinder.repository.ReleventJobRepository;
import aj.finder.ajfinder.service.ReleventJobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest
@Import(ReleventJobService.class)
public class ReleventJobServiceTest {

    @Autowired
    private ReleventJobRepository releventJobRepository;

    @Autowired
    private ReleventJobService releventJobService;

    @Test
    void testGetAllJobs() {
        // Clear repository and add test data
        ReleventJob job1 = new ReleventJob("http://example.com/1", "Headline 1", "Description 1", "Cover 1");
        ReleventJob job2 = new ReleventJob("http://example.com/2", "Headline 2", "Description 2", "Cover 2");

        Flux<ReleventJob> setup = releventJobRepository.deleteAll()
                .thenMany(releventJobRepository.saveAll(Flux.just(job1, job2)));

        StepVerifier.create(setup)
                .expectNextCount(2)
                .verifyComplete();

        // Test service call
        Flux<ReleventJob> result = releventJobService.getAllJobs();

        StepVerifier.create(result)
                .expectNextMatches(job -> job.getHeadline().equals("Headline 1"))
                .expectNextMatches(job -> job.getHeadline().equals("Headline 2"))
                .verifyComplete();
    }
}
