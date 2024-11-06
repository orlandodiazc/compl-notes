package com.ditod.notes.domain.verification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationRepository extends JpaRepository<Verification, UUID> {
    Optional<Verification> findByTypeAndTarget(VerifyType verifyType, String target);

    void deleteByTypeAndTarget(VerifyType verifyType, String target);
}
