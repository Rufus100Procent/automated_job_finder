package aj.finder.ajfinder.modal.platsbanken;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "job_ads")
public class JobAd {
    @Id
    private String id;
    private String description;
    private String headline;
    private String webpageUrl;
    private LocalDateTime applicationDeadline;
}


