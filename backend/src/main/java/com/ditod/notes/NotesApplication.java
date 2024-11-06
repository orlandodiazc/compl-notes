package com.ditod.notes;

import com.ditod.notes.config.ProtectorProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableConfigurationProperties({ProtectorProperties.class})
@OpenAPIDefinition(info = @Info(title = "Compl notes", description = "REST like web application to keep track of " +
        "user" + " notes.", contact = @Contact(name = "Orlando Diaz", email = "orlandodiazconde@gmail.com")))
@EnableJpaAuditing
public class NotesApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotesApplication.class, args);
    }
}
