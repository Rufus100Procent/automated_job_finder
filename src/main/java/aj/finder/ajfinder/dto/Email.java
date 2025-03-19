package aj.finder.ajfinder.dto;

import aj.finder.ajfinder.modal.ReleventJob;
import lombok.Data;

import java.util.List;

@Data
public class Email {
    private String to;
    private String subject;
    private List<ReleventJob> jobs;
}
