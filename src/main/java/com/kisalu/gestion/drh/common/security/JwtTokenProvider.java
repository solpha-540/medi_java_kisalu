package com.kisalu.gestion.drh.common.security;

import com.kisalu.gestion.drh.common.dto.TokenPair;
import com.kisalu.gestion.drh.model.SysUsers;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestion JWT (equivalent PHP : app/controllers/token.controller.php).
 * Payload : user_login, user_rf, user_id, user_type, sub (AUTH|REFRESH).
 */
@Component
public class JwtTokenProvider {


    private final JwtProperties properties;
    private String state;
    private Map<String, Object> error;
    private Claims decodedToken;

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String encode(Map<String, Object> payload) {
        return Jwts.builder()
                .claims(payload)
                .signWith(secretKey())
                .compact();
    }

    public TokenPair generateTokens(SysUsers user) {
        long now = System.currentTimeMillis();
        long exp = now + properties.getDuration() * 1000L;

        Map<String, Object> access = new HashMap<>();
        access.put("user_login", user.getLogin());
        access.put("user_rf", user.getIdGenerate());
        access.put("user_id", user.getId());
        access.put("user_type", user.getUserType());
        access.put("iat", now / 1000);
        access.put("exp", exp / 1000);
        access.put("sub", properties.getAccessSub());

        Map<String, Object> refresh = new HashMap<>();
        refresh.put("user_login", user.getLogin());
        refresh.put("user_rf", user.getIdGenerate());
        refresh.put("user_id", user.getId());
        refresh.put("user_type", user.getUserType());
        refresh.put("iat", now / 1000);
        refresh.put("exp", exp / 1000);
        refresh.put("sub", properties.getRefreshSub());

        return new TokenPair(encode(access), encode(refresh));
    }

    public Claims decode(String token) {
        try {
            decodedToken = Jwts.parser()
                    .verifyWith(secretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            state = "decoded";
            error = null;
            return decodedToken;
        } catch (ExpiredJwtException ex) {
            state = "expired";
            error = Map.of("code", 401, "message", ex.getMessage());
            return null;
        } catch (JwtException ex) {
            state = "invalid";
            error = Map.of("code", 0, "message", ex.getMessage());
            return null;
        } catch (Exception ex) {
            state = "error";
            error = Map.of("code", 0, "message", ex.getMessage());
            return null;
        }
    }

    public boolean verifyToken(String token) {
        if (token == null) {
            state = "non-existent";
            error = Map.of("code", 0, "message", "unexistant token");
            return false;
        }
        return decode(token) != null;
    }

    public String getTokenType(String token) {
        if (!verifyToken(token)) {
            return null;
        }
        return properties.getRefreshSub().equals(decodedToken.getSubject()) ? "refresh" : "access";
    }

    public AuthenticatedUser toAuthenticatedUser(Claims claims) {
        return new AuthenticatedUser(
                claims.get("user_login", String.class),
                claims.get("user_rf", String.class),
                claims.get("user_id", Integer.class),
                claims.get("user_type", String.class));
    }

    public String getState() {
        return state;
    }

    public Map<String, Object> getError() {
        return error;
    }

    public Claims getDecodedToken() {
        return decodedToken;
    }

    public String extractTokenFromHeader(String header) {
        if (header == null || header.isBlank()) {
            return null;
        }
        if (header.toLowerCase().startsWith("tac ")) {
            return header.substring(4).trim();
        }
        return null;
    }

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
    }
}
