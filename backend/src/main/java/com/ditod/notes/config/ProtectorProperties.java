package com.ditod.notes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "protector")
public record ProtectorProperties(String corsAllowedOrigin,
                                  String csrfCookieDomain) {

}