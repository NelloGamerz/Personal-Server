package com.example.Personal_Server.Service;

import org.springframework.stereotype.Service;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;

@Service
public class TotpService {
    
    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final TimeProvider timeprovider = new SystemTimeProvider();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private final CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeprovider);

    public String generateSecret(){
        return secretGenerator.generate();
    }

    public String createTotpUri(String userEmail, String secret, String issuer){
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&period=30&digits=6",
                issuer,
                userEmail,
                secret,
                issuer
        );
    }

    public boolean verify(String secret, String code){
        return codeVerifier.isValidCode(secret, code);
    }
}
