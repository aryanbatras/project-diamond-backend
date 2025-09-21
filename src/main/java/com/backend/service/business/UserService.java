package com.backend.service.business;

import com.backend.service.config.FirebaseConfig;
import com.backend.service.model.UserModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Service
public class UserService {

    @Autowired
    private Firestore firestore;

    @Autowired

    private FirebaseAuth firebaseAuth;

    @Autowired
    private Executor asyncExecutor;

    public Map<String, Object> decodeFirebaseToken(String idToken) throws Exception {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);

            String uid = decodedToken.getUid();

            Object roleObj = firebaseAuth.getUser(uid).getCustomClaims().get("role");
            String role = (roleObj != null) ? roleObj.toString() : "visitor";

            // Prepare user info response
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("uid", uid);
            userInfo.put("email", decodedToken.getEmail());

            // If the returned role is null, set it to "visitor"
            // AUTHORIZATION
            userInfo.put("role", role);
            
            // Process Firestore operations asynchronously
            String finalRole = role;
            asyncExecutor.execute(() -> {
                try {
                    checkAndCreateUser(decodedToken, finalRole);
                } catch (Exception e) {
                    System.err.println("Error processing user in Firestore: " + e.getMessage());
                }
            });
            
            return userInfo;
        } catch (FirebaseAuthException e) {
            throw new Exception("Invalid token");
        }
    }
    
    @Async
    private void checkAndCreateUser(FirebaseToken decodedToken, String role) throws ExecutionException, InterruptedException, FirebaseAuthException {
        String uid = decodedToken.getUid();
        DocumentReference userRef = firestore.collection("users").document(uid);
        ApiFuture<DocumentSnapshot> future = userRef.get();
        DocumentSnapshot document = future.get();

        if (firebaseAuth.getUser(uid).getCustomClaims().get("role") == null) {
            firebaseAuth.setCustomUserClaims(uid, Map.of("role", role));
        }

        if (!document.exists()) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", uid);
            userData.put("email", decodedToken.getEmail());
            userData.put("displayName", decodedToken.getName() != null ? decodedToken.getName() : "");
            userData.put("role", role);

            Map<String, Object> profile = new HashMap<>();
            profile.put("college", "");
            profile.put("year", "");
            profile.put("avatarUrl", decodedToken.getPicture() != null ? decodedToken.getPicture() : "");
            userData.put("profile", profile);
            
            Map<String, Object> courseTrack = new HashMap<>();
            userData.put("courseTrack", courseTrack);
            
            userData.put("createdAt", FieldValue.serverTimestamp());
            
            userRef.set(userData);

        }
    }

    public Map<String, Object> requestCreator(String idToken) throws Exception {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            // Get current claims in a single operation
            Map<String, Object> currentClaims = firebaseAuth.getUser(uid).getCustomClaims();
            String currentRole = (String) currentClaims.getOrDefault("role", "visitor");

            // Only allow visitors to request creator role
            if (!"visitor".equals(currentRole)) throw new IllegalStateException("Only users with 'visitor' role can request creator access");

            // Update Firebase Auth claims
            Map<String, Object> newClaims = new HashMap<>(currentClaims); // Preserve existing claims
            newClaims.put("role", "creator");
            firebaseAuth.setCustomUserClaims(uid, newClaims);

            // Update Firestore
            try {
                firestore.collection("users").document(uid)
                        .update("role", "creator")
                        .get(); // Wait for completion
            } catch (Exception e) {
                // Revert claims if Firestore update fails
                firebaseAuth.setCustomUserClaims(uid, currentClaims);
                throw new Exception("Failed to update Firestore", e);
            }

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully upgraded to creator role");
            response.put("uid", uid);
            response.put("newRole", "creator");

            return response;

        } catch (FirebaseAuthException e) {
            throw new Exception("Authentication error: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Failed to process creator request: " + e.getMessage());
        }
    }

}