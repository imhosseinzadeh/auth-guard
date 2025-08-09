package com.imho.authguard.infra.i18n;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessageResolver {

    private final MessageSource messageSource;

    /**
     * Resolves a localized message by its key using the current Locale.
     * Falls back to the key itself if the message is not found.
     *
     * @param key  the message key
     * @param args optional arguments for message formatting
     * @return localized message or key if not found
     */
    public String getMessage(String key, Object... args) {
        return getMessage(key, LocaleContextHolder.getLocale(), args);
    }

    /**
     * Resolves a localized message by its key using a provided Locale.
     * Falls back to the key itself if the message is not found.
     *
     * @param key    the message key
     * @param locale the desired locale
     * @param args   optional arguments for message formatting
     * @return localized message or key if not found
     */
    public String getMessage(String key, Locale locale, Object... args) {
        try {
            return messageSource.getMessage(key, args, locale);
        } catch (NoSuchMessageException e) {
            logMissingKey(key, locale);
            return key;
        }
    }

    private void logMissingKey(String key, Locale locale) {
        log.warn("Missing message key: {} for locale: {}", key, locale);
    }

}
