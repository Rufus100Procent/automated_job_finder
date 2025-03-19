package aj.finder.ajfinder.modal;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "releventJobs")
@Data
public class ReleventJob {

    @Id
    private String id;
    private String urlLink;
    private String description;
    private String coverLatter;

}
