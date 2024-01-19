package com.sandun.web.util;

import com.sandun.web.entities.User;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import io.fusionauth.jwt.hmac.HMACVerifier;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JWTUtil {
    private static final String CLAIM_KEY_ID = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String ISSUER = "www.sandun.com";
    private static final String SECRET = "fSpZlCdvDKlbMaNkY3w7wseb5piW9DLEWuN19qjp5BZX7j6AyokbbtH35w7T5bmV9zFCrvHOx25aYeLyS7Gm14rLbhexROFHCRnkY2WB5LSiEgKPSUqsBDfEHhvmd99scLLSNloHmP7FGBdrDvGVPzghVwTXFZWExSIFO3S8nQv73vakrVqiFTAx7SpmG6JWwaPI0c2W0MPrpTd2AbbZhJAU2UFLaDDsT0OXHUE3kylMtl5S0t4MDMY2p0WjyCoxcKHO9goykT5mpWugKWAO2BupRngBVLXQlbkLRbYjpTqsGxt4CDZkbdaVFptyKVyP";
    //    private static final Long TOKEN_LIFE = 60L * 6L;
    private static final Long TOKEN_LIFE = 60L * 24L;
    private static final Long REFRESH_TOKEN_LIFE = 60L * 24L;

    private String generateToken(Map<String, String> claims, Long expiration, String subject) {
        Signer signer = HMACSigner.newSHA256Signer(SECRET);
        JWT jwt = new JWT().setIssuer(ISSUER).setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC)).setSubject(subject).setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(expiration));
        claims.keySet().forEach(k -> {
            if (claims.get(k) != null) {
                jwt.addClaim(k, claims.get(k));
            }
        });
        return JWT.getEncoder().encode(jwt, signer);
    }

    public Map<String, String> getClaimsFromToken(String token) {
        Verifier verifier = HMACVerifier.newVerifier(SECRET);
        JWT jwt = JWT.getDecoder(). decode(token, verifier);
        Map<String, String> claims = jwt.getAllClaims().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
        return claims;
    }

    public String getUserIdFromToken(String token) {
        Map<String, String> claims = getClaimsFromToken(token);
        return claims.get(CLAIM_KEY_ID);
    }

    public Date getExpireDateFromToken(String token) {
        Verifier verifier = HMACVerifier.newVerifier(SECRET);
        JWT jwt = JWT.getDecoder().decode(token, verifier);
        return Date.from(jwt.expiration.toInstant());
    }

    private boolean isTokenExpired(String token) {
        Date date = getExpireDateFromToken(token);
        return date.before(new Date());
    }

    public boolean validateToken(String token, User user) {
        String id = getUserIdFromToken(token);
        try {
            return id.equals(user.getId()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String generateAccessToken(User user) {
        Map<String, String> claims = new HashMap<>();
        claims.put(CLAIM_KEY_ID, String.valueOf(user.getId()));
        claims.put("userFName", user.getEmailOrCno());
        claims.put(CLAIM_KEY_CREATED, new Date().toString());
        return generateToken(claims, TOKEN_LIFE, String.valueOf(user.getId()));
    }

    public String generateRefreshToken(User user) {
        Map<String, String> claims = new HashMap<>();
        claims.put(CLAIM_KEY_ID, String.valueOf(user.getId()));
        claims.put(CLAIM_KEY_CREATED, new Date().toString());
        return generateToken(claims, REFRESH_TOKEN_LIFE, String.valueOf(user.getId()));
    }

}
