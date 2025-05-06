package aj.finder.ajfinder.modal;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "releventJobs")
@Data
@Getter
@Setter
public class ReleventJob {

    @Id
    private String id;

    @Field("webpage_url")
    private String webpageUrl;
    private String headline;
    private String description;
    private String coverLatter;
    private boolean sent;

    public ReleventJob(String webpageUrl, String headline, String description, String coverLatter) {
        this.webpageUrl = webpageUrl;
        this.headline = headline;
        this.description = description;
        this.coverLatter = coverLatter;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWebpageUrl() {
        return webpageUrl;
    }

    public void setWebpageUrl(String webpageUrl) {
        this.webpageUrl = webpageUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverLatter() {
        return coverLatter;
    }

    public void setCoverLatter(String coverLatter) {
        this.coverLatter = coverLatter;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
