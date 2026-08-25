package com.samtar.notificationservice.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.samtar.notificationservice.template.EmailTemplateRenderer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class EmailService {

    private final Resend resend;
    private final String from;

    public EmailService(@Value("${notification.mail.api-key}") String apiKey,
                        @Value("${notification.mail.from}") String from) {
        this.resend = new Resend(apiKey);
        this.from = from;
    }

    /**
     * Renders the classpath template with the given variables and sends it via the Resend API.
     * Throws on failure so the Kafka listener can let the DefaultErrorHandler retry / DLT it.
     */
    public void sendHtml(String to, String subject, String templateLocation, Map<String, String> variables) {
        String body = EmailTemplateRenderer.render(templateLocation, variables);
        CreateEmailOptions options = CreateEmailOptions.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .html(body)
                .build();
        try {
            CreateEmailResponse response = resend.emails().send(options);
            log.info("Sent '{}' email to {} (id={})", subject, to, response.getId());
        } catch (ResendException e) {
            throw new IllegalStateException("Failed to send email '" + subject + "' to " + to, e);
        }
    }
}
