package com.imho.authguard.infra.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Profile("dev")
@Component
@Slf4j
public class FakeEmailService implements EmailService {

    @Override
    public void sendEmail(String to, MessageType type, Map<String, Object> variables) {
        log.info("[FAKE EMAIL - DEV MODE] Sending email of type: {} to: {}\nVariables: {}", type, to, variables);
    }
}

