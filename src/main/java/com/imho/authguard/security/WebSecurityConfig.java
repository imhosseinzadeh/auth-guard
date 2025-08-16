package com.imho.authguard.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imho.authguard.security.authentication.filter.JwtAuthenticationFilter;
import com.imho.authguard.security.authorization.JwtAuthorizationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(userDetailsService);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(true);

        return providerManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager, JwtUtil jwtUtil, ObjectMapper objectMapper) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requestMatcherRegistry ->
                        requestMatcherRegistry
                                .requestMatchers(
                                        PathPatternRequestMatcher.pathPattern("/"),
                                        PathPatternRequestMatcher.pathPattern("/error"),
                                        PathPatternRequestMatcher.pathPattern("/index.html"),
                                        PathPatternRequestMatcher.pathPattern("/v3/api-docs/**"),
                                        PathPatternRequestMatcher.pathPattern("/swagger-ui/**"),
                                        PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/actuator/health"),
                                        PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/api/v1/users"), // register api
                                        PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/api/v1/auth/email-verification"),
                                        PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/api/v1/tokens/refresh"),
                                        JwtAuthenticationFilter.LOGIN_MATCHER)
                                .permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager, jwtUtil, objectMapper), AnonymousAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(jwtUtil), AuthorizationFilter.class)
                .build();
    }

}
