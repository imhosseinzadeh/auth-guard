package com.imho.authguard.core.notification;

import java.util.Map;

public interface EmailService {

    void sendEmail(String to, MessageType type, Map<String, Object> variables);

}
