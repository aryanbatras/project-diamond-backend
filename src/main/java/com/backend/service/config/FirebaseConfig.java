package com.backend.service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@EnableAsync
public class FirebaseConfig {

    @Bean
    public Firestore firestore() throws IOException {
        if(FirebaseApp.getApps().isEmpty()) FirebaseApp.initializeApp();
        return FirestoreClient.getFirestore();
    }

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        if(FirebaseApp.getApps().isEmpty()) FirebaseApp.initializeApp();
        return FirebaseAuth.getInstance();
    }

}



