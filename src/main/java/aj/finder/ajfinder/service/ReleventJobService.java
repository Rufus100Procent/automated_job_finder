package aj.finder.ajfinder.service;

import aj.finder.ajfinder.modal.ReleventJob;
import aj.finder.ajfinder.repository.ReleventJobRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ReleventJobService {

    private final ReleventJobRepository releventJobRepository;

    public ReleventJobService(ReleventJobRepository releventJobRepository) {
        this.releventJobRepository = releventJobRepository;
    }

    public Flux<ReleventJob> getAllJobs() {
        return releventJobRepository.findAll();
    }

}
