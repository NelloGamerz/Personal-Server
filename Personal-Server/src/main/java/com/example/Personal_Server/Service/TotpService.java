package com.example.Personal_Server.Service;

import org.springframework.stereotype.Service;

import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;

@Service
public class TotpService {
    
    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();

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
}
