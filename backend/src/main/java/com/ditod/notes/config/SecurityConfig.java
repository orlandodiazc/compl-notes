package com.ditod.notes.config;

import com.ditod.notes.auth.JpaUserDetailsService;
import com.ditod.notes.protection.CsrfCookieFilter;
import com.ditod.notes.protection.SpaCsrfTokenRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final JpaUserDetailsService jpaUserDetailsService;
    private final ProtectorProperties protectorProperties;

    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService, ProtectorProperties protectorProperties) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.protectorProperties = protectorProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var CsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRepository.setCookieCustomizer(
                c -> c.secure(true).httpOnly(false).domain(protectorProperties.csrfCookieDomain()).path("/"));
        http.cors(Customizer.withDefaults());
        http.csrf((csrf) -> csrf.csrfTokenRepository(CsrfTokenRepository)
                                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
        http.authorizeHttpRequests(
                auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().requestMatchers("/auth/*")
                            .permitAll().requestMatchers(HttpMethod.POST, "/users/{username}/notes/**")
                            .access(new WebExpressionAuthorizationManager(
                                    "hasAuthority('CREATE:NOTE:ANY') or (hasAuthority('CREATE:NOTE:OWN') and " +
                                            "#username == authentication.name)"))
                            .requestMatchers(HttpMethod.PUT, "/users/{username}/notes/**")
                            .access(new WebExpressionAuthorizationManager(
                                    "hasAuthority('UPDATE:NOTE:ANY') or (hasAuthority('UPDATE:NOTE:OWN') and " +
                                            "#username == authentication.name)"))
                            .requestMatchers(HttpMethod.DELETE, "/users/{username}/notes/**")
                            .access(new WebExpressionAuthorizationManager(
                                    "hasAuthority('DELETE:NOTE:ANY') or (hasAuthority('DELETE:NOTE:OWN') and " +
                                            "#username == authentication.name)"))
                            .requestMatchers(HttpMethod.GET).permitAll().anyRequest().authenticated());
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(protectorProperties.corsAllowedOrigin()));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
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
    public UserDetailsService userDetailsService() {
        return jpaUserDetailsService;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


