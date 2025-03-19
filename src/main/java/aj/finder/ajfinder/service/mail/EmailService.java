package aj.finder.ajfinder.service.mail;

import aj.finder.ajfinder.dto.Email;
import aj.finder.ajfinder.modal.ReleventJob;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public Mono<Void> sendEmail(Email email) {
        return Mono.fromRunnable(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(email.getTo());
                helper.setSubject(email.getSubject());
                helper.setText(buildHtmlContent(email), true);

                mailSender.send(message);
            } catch (MessagingException e) {
                throw new RuntimeException("Failed to send email", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    private String buildHtmlContent(Email email) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>Jobs you Are Qualified For</h2>");

        for (ReleventJob job : email.getJobs()) {
            html.append("<div style='border: 1px solid #ddd; padding: 10px; margin-bottom: 10px;'>");
            html.append("<h3 style='color: #007bff;'>").append(job.getTitle()).append("</h3>");
            html.append("<p><strong>URL:</strong> <a href='").append(job.getUrlLink()).append("'>")
                    .append(job.getUrlLink()).append("</a></p>");
            html.append("<p><strong>Description:</strong> ").append(job.getDescription()).append("</p>");
            html.append("<p><strong>Cover Letter:</strong> ").append(job.getCoverLatter()).append("</p>");
            html.append("</div>");
        }

        html.append("</body></html>");
        return html.toString();
    }

}
