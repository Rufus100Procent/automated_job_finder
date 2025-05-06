package aj.finder.ajfinder.dto.platsbanken;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobHitDto {

    private String id;
    private int totalResult;
    private String webpage_url;
    private String headline;
    private DescriptionDto description;

    public JobHitDto(String id, String webpage_url, String headline, DescriptionDto description) {
        this.id = id;
        this.webpage_url = webpage_url;
        this.headline = headline;
        this.description = description;
    }
}
