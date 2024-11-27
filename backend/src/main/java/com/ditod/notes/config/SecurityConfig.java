package com.ditod.notes.config;

import com.ditod.notes.security.protection.SpaCsrfTokenRequestHandler;
import com.ditod.notes.security.JpaUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final JpaUserDetailsService jpaUserDetailsService;

    @Value("${cors-allowed-origin}")
    private String corsAllowedOrigin;

    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService) {
        this.jpaUserDetailsService = jpaUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()));
        http.userDetailsService(jpaUserDetailsService);
        http.authorizeHttpRequests(
                auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers("/auth/*").permitAll()
                            .requestMatchers(HttpMethod.POST, "/users/{username}/notes/**")
                            .access(new WebExpressionAuthorizationManager(
                                    "hasAuthority('CREATE:NOTE:ANY') or (hasAuthority('CREATE:NOTE:OWN') and " + "#username == authentication.name)"))
                            .requestMatchers(HttpMethod.PUT, "/users/{username}/notes/**")
                            .access(new WebExpressionAuthorizationManager(
                                    "hasAuthority('UPDATE:NOTE:ANY') or (hasAuthority('UPDATE:NOTE:OWN') and " + "#username == authentication.name)"))
                            .requestMatchers(HttpMethod.DELETE, "/users/{username}/notes/**")
                            .access(new WebExpressionAuthorizationManager(
                                    "hasAuthority('DELETE:NOTE:ANY') or (hasAuthority('DELETE:NOTE:OWN') and " + "#username == authentication.name)"))
                            .requestMatchers(HttpMethod.GET).permitAll().anyRequest()
                            .authenticated());
        return http.build();
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(corsAllowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}


