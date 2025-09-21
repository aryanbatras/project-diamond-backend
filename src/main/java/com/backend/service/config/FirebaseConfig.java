package com.backend.service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableAsync
public class FirebaseConfig {

    @Value("${FIREBASE_CREDENTIALS_BASE64}")
    private String firebaseCredentialsBase64;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                String decodedJson = new String(
                    Base64.getDecoder().decode(firebaseCredentialsBase64), 
                    StandardCharsets.UTF_8
                );
                
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                        new ByteArrayInputStream(decodedJson.getBytes(StandardCharsets.UTF_8))))
                    .setProjectId("project-diamond-a636c")
                    .build();
                
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Firebase: " + e.getMessage(), e);
        }
    }

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}



