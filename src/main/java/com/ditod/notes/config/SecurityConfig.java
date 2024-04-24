package com.ditod.notes.config;

import com.ditod.notes.auth.JpaUserDetailsService;
import com.ditod.notes.protection.CsrfCookieFilter;
import com.ditod.notes.protection.SpaCsrfTokenRequestHandler;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final JpaUserDetailsService jpaUserDetailsService;
    private final RsaKeyProperties rsaKeys;


    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService,
            RsaKeyProperties rsaKeys) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.rsaKeys = rsaKeys;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {
        http.cors(c -> {
            c.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:4173", "https://compl-notes.onrender.com/", "https://compl.fly.dev/"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            });
        });
        http.csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(AbstractHttpConfigurer::disable);
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/logout")
                .authenticated()
                .requestMatchers("/auth/*")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/users/{username}/notes/**")
                .access(new WebExpressionAuthorizationManager("hasAuthority('SCOPE_CREATE:NOTE:ANY') or (hasAuthority('SCOPE_CREATE:NOTE:OWN') and #username == authentication.name)"))
                .requestMatchers(HttpMethod.PUT, "/users/{username}/notes/**")
                .access(new WebExpressionAuthorizationManager("hasAuthority('SCOPE_UPDATE:NOTE:ANY') or (hasAuthority('SCOPE_UPDATE:NOTE:OWN') and #username == authentication.name)"))
                .requestMatchers(HttpMethod.DELETE, "/users/{username}/notes/**")
                .access(new WebExpressionAuthorizationManager("hasAuthority('SCOPE_DELETE:NOTE:ANY') or (hasAuthority('SCOPE_DELETE:NOTE:OWN') and #username == authentication.name)"))
                .requestMatchers(HttpMethod.GET)
                .permitAll()
                .anyRequest()
                .authenticated());
        return http.build();
    }


    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jpaUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


