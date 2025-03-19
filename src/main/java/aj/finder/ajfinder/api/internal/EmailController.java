package aj.finder.ajfinder.api.internal;

import aj.finder.ajfinder.dto.Email;
import aj.finder.ajfinder.service.mail.EmailService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v0/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public Mono<Void> sendEmail(@RequestBody Email email) {
        return emailService.sendEmail(email);
    }
}
