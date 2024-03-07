package com.imho.authguard.adapter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation.
 */
@Configuration
public class OpenApiConfiguration {

    /**
     * Creates an OpenAPI bean with the provided application information.
     *
     * @return An OpenAPI object containing application information.
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title(ApplicationInfoUtil.TITLE)
                .description(ApplicationInfoUtil.DESCRIPTION)
                .version(ApplicationInfoUtil.VERSION)
                .license(ApplicationInfoUtil.LICENSE)
                .contact(ApplicationInfoUtil.CONTACT));
    }

    /**
     * Utility class for providing application information for OpenAPI documentation.
     */
    @UtilityClass
    private static class ApplicationInfoUtil {

        public static final String TITLE = "Auth Guard";
        public static final String DESCRIPTION = "AuthGuard is a robust user authentication and authorization application";
        public static final String VERSION = "1.0";

        private static final String CONTACT_NAME = "Iman Hosseinzadeh";
        private static final String CONTACT_EMAIL = "Imhosseinzadeh@gmail.com";
        private static final String CONTACT_URL = "https://ImanHosseinzadeh.com";

        /**
         * Contact information for the application.
         */
        public static final Contact CONTACT = new Contact()
                .name(CONTACT_NAME)
                .email(CONTACT_EMAIL)
                .url(CONTACT_URL);

        private static final String LICENSE_NAME = "License 2024";
        private static final String LICENSE_URL = "License Url";

        /**
         * License information for the application.
         */
        public static final License LICENSE = new License()
                .name(LICENSE_NAME)
                .url(LICENSE_URL);

    }
}
