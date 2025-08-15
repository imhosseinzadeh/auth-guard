package com.imho.authguard.infra.notification;

import com.imho.authguard.common.util.MaskingUtils;
import com.imho.authguard.exception.infrastructure.MailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Profile("prod")
@Component
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public static final String DEFAULT_FROM_ADDRESS = "noreply@authguard.com";

    @Async
    @Override
    public void sendEmail(String to, MessageType type, Map<String, Object> variables) {
        type.validateVariables(variables);

        final String subject = type.getSubject();
        final String templatePath = type.resolveTemplatePath();

        log.debug("Preparing to send email to '{}' using template '{}'", MaskingUtils.maskEmail(to), templatePath);

        try {
            String htmlContent = renderTemplate(templatePath, variables);
            MimeMessage message = buildMimeMessage(to, subject, htmlContent);
            mailSender.send(message);

            log.info("Email successfully sent to '{}' with subject '{}'", MaskingUtils.maskEmail(to), subject);
        } catch (MessagingException | MailException e) {
            handleSendFailure(to, e);
        }
    }

    private String renderTemplate(String templatePath, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templatePath, context);
    }

    private MimeMessage buildMimeMessage(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(DEFAULT_FROM_ADDRESS);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        return message;
    }

    private void handleSendFailure(String to, Exception e) {
        String errorMessage = String.format("Failed to send email to '%s'. Reason: %s", to, e.getMessage());
        log.error(errorMessage, e);
        throw new MailSendingException("Email Delivery Failed", errorMessage, e);
    }

}
