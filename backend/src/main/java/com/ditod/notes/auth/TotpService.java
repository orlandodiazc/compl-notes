package com.ditod.notes.auth;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Service;

@Service
public class TotpService {
    private static final int TIME_PERIOD = 900;

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();

    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();

    private final TimeProvider timeProvider = new SystemTimeProvider();

    private final DefaultCodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

    public TotpService() {
        codeVerifier.setTimePeriod(TIME_PERIOD);
    }

    public CodeGenerationResult generate() {
        String secret = secretGenerator.generate();
        String code = null;
        try {
            code = codeGenerator.generate(secret, Math.floorDiv(timeProvider.getTime(), TIME_PERIOD));
        } catch (CodeGenerationException e) {
            throw new RuntimeException(e);
        }
        return new CodeGenerationResult(secret, code);
    }

    public boolean isValidCode(String secret, String code) {

        return codeVerifier.isValidCode(secret, code);
    }

    public record CodeGenerationResult(String secret, String code) {}
}
