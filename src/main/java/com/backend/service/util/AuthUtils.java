package com.backend.service.util;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class AuthUtils {
    private AuthUtils() {}

    public static String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
        }
        String header = authorizationHeader.trim();
        if (header.toLowerCase().startsWith("bearer ")) {
            String token = header.substring(7).trim();
            if (token.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
            }
            return token;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Authorization header format");
    }

    public static String extractRoleFromToken(FirebaseToken decodedToken) {
        if (decodedToken == null) return null;
        Object roleObj = decodedToken.getClaims() == null ? null : decodedToken.getClaims().get("role");
        return roleObj != null ? roleObj.toString() : null;
    }
}
