package com.backend.service.controller;

import com.backend.service.util.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.backend.service.business.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String firebaseToken = AuthUtils.extractBearerToken(authorizationHeader);
            Map<String, Object> response = userService.decodeFirebaseToken(firebaseToken);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing login");
        }
    }

    @PostMapping("/request-creator")
    public ResponseEntity<Map<String, Object>> requestCreator(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String firebaseToken = AuthUtils.extractBearerToken(authorizationHeader);
        try {
            Map<String, Object> response = userService.requestCreator(firebaseToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Request creator error: {}", e.getMessage(), e);
            String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
