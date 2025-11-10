package com.rakesh.finflow.identity.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public String generateAccessToken(String username) throws JOSEException {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(900))) // 15 min
                .issuer("identity-service")
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        jwt.sign(new RSASSASigner(privateKey));

        return jwt.serialize();
    }

    public boolean validateAccessToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(new RSASSAVerifier(publicKey))) return false;
            Date expiry = jwt.getJWTClaimsSet().getExpirationTime();
            return expiry.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromJWT(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            return jwt.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}

