package com.imho.authguard.config.security;

import com.imho.authguard.authentication.EmailPasswordAuthenticationProvider;
import com.imho.authguard.authentication.jwt.EmailPasswordAuthenticationFilter;
import com.imho.authguard.authentication.jwt.JwtUtil;
import com.imho.authguard.authorization.JWTAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        EmailPasswordAuthenticationProvider authenticationProvider =
                new EmailPasswordAuthenticationProvider(userDetailsService, passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(true);

        return providerManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager, JwtUtil jwtUtil) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requestMatcherRegistry ->
                        requestMatcherRegistry
                                .requestMatchers(
                                        antMatcher("/"),
                                        antMatcher("/index.html"),
                                        antMatcher("/v3/api-docs/**"),
                                        antMatcher("/swagger-ui/**"),
                                        EmailPasswordAuthenticationFilter.LOGIN_ANT_PATH_REQUEST_MATCHER
                                )
                                .permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new EmailPasswordAuthenticationFilter(authenticationManager, jwtUtil), AnonymousAuthenticationFilter.class)
                .addFilterBefore(new JWTAuthorizationFilter(jwtUtil), AuthorizationFilter.class)
                .build();
    }

}