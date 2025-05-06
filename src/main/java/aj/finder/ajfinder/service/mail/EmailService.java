package aj.finder.ajfinder.service.mail;

import aj.finder.ajfinder.modal.ReleventJob;
import aj.finder.ajfinder.repository.ReleventJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.List;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final ReleventJobRepository releventJobRepository;

    public EmailService(JavaMailSender mailSender, ReleventJobRepository releventJobRepository) {
        this.mailSender = mailSender;
        this.releventJobRepository = releventJobRepository;
    }

    public Mono<Void> sendAllRelevantJobsEmail(String toEmail) {
        return releventJobRepository.findAll()
                .filter(job -> !job.isSent())
                .collectList()
                .flatMap(uniqueJobs -> {
                    // Don't send email if there's no new job.
                    if (uniqueJobs.isEmpty()) {
                        log.info("No new relevant jobs found, email will not be sent.");
                        return Mono.empty();
                    }
                    log.info("Total unique jobs to send: {}", uniqueJobs.size());
                    return Mono.fromRunnable(() -> {
                                try {
                                    MimeMessage message = mailSender.createMimeMessage();
                                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                                    helper.setTo(toEmail);
                                    helper.setSubject("Jobs You Are Qualified For (Total: " + uniqueJobs.size() + ")");
                                    helper.setText(buildHtmlContent(uniqueJobs), true);
                                    mailSender.send(message);
                                    log.info("Email sent successfully to {}", toEmail);
                                } catch (MessagingException e) {
                                    log.error("Failed to send email", e);
                                    throw new RuntimeException("Failed to send email", e);
                                }
                            }).subscribeOn(Schedulers.boundedElastic())
                            .then(Mono.just(uniqueJobs));
                })
                // After successful email, update the sent flag.
                .flatMapMany(Flux::fromIterable)
                .flatMap(job -> {
                    job.setSent(true);
                    log.info("Marking job {} as sent.", job.getWebpageUrl());
                    return releventJobRepository.save(job);
                })
                .then();
    }

    private String buildHtmlContent(List<ReleventJob> jobs) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>")
                .append("<h2>Jobs You Are Qualified For</h2>")
                .append("<p>Total jobs waiting for review: ").append(jobs.size()).append("</p>");
        for (ReleventJob job : jobs) {
            html.append("<div style='border: 1px solid #ddd; padding: 10px; margin-bottom: 10px;'>")
                    .append("<h3 style='color: #007bff;'>").append(job.getHeadline()).append("</h3>")
                    .append("<p><strong>URL:</strong> <a href='").append(job.getWebpageUrl()).append("'>")
                    .append(job.getWebpageUrl()).append("</a></p>")
                    .append("<p><strong>Description:</strong> ").append(job.getDescription()).append("</p>")
                    .append("<p><strong>Cover Letter:</strong> ").append(job.getCoverLatter()).append("</p>")
                    .append("</div>");
        }
        html.append("</body></html>");
        return html.toString();
    }
}
