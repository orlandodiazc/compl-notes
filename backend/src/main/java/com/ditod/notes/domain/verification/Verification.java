package com.ditod.notes.domain.verification;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"target", "type"})})
@EntityListeners(AuditingEntityListener.class)
public class Verification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String secret;

    @NotNull
    private VerifyType type;

    @NotNull
    private String target;

    @NotNull
    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    public Verification() {
    }

    public Verification(String secret, VerifyType type, String target) {
        this.secret = secret;
        this.type = type;
        this.target = target;
    }

    public UUID getId() {
        return id;
    }

    public @NotNull String getSecret() {
        return secret;
    }

    public void setSecret(@NotNull String secret) {
        this.secret = secret;
    }

    public @NotNull VerifyType getType() {
        return type;
    }

    public void setType(@NotNull VerifyType type) {
        this.type = type;
    }

    public @NotNull String getTarget() {
        return target;
    }

    public void setTarget(@NotNull String target) {
        this.target = target;
    }

    public @NotNull Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Verification{" + "id=" + id + ", secret='" + secret + '\'' + ", type='" + type + '\'' + ", target='" + target + '\'' + ", createdAt=" + createdAt + '}';
    }
}
