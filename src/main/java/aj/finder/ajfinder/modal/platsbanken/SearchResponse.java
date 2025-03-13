package aj.finder.ajfinder.modal.platsbanken;


import lombok.Data;
import java.util.List;

@Data
public class SearchResponse {
    private List<JobAd> jobs;
}
