package com.backend.service.controller;

import com.backend.service.business.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        String firebaseToken = authorizationHeader.split("Bearer ")[1];
        Map<String, Object> response = userService.decodeFirebaseToken(firebaseToken);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/request-creator")
    public ResponseEntity<Map<String, Object>> requestCreator(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        String firebaseToken = authorizationHeader.split("Bearer ")[1];
        Map<String, Object> response = userService.requestCreator(firebaseToken);
        return ResponseEntity.ok().body(response);
    }

}
