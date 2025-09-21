package com.backend.service.business;

import com.backend.service.model.UserModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private Firestore firestore;

    @Autowired
    private FirebaseAuth firebaseAuth;

    @Autowired
    private Executor asyncExecutor;

    public Map<String, Object> decodeFirebaseToken(String idToken) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            String uid = decodedToken.getUid( );
            Map<String, Object> claims = decodedToken.getClaims( );

            Object roleObj = claims != null ? claims.get("role") : null;
            String role = roleObj != null ? roleObj.toString( ) : "visitor";

            Map<String, Object> userInfo = new HashMap<>( );
            userInfo.put("uid", uid);
            userInfo.put("email", decodedToken.getEmail( ));
            userInfo.put("name", decodedToken.getName( ));
            userInfo.put("picture", decodedToken.getPicture( ));
            userInfo.put("role", role);

            asyncExecutor.execute(() -> {
                try {
                    checkAndCreateUser(decodedToken, role);
                } catch (Exception e) {
                    logger.error("Error processing user in Firestore: {}", e.getMessage( ), e);
                }
            });

            return userInfo;
        } catch (FirebaseAuthException e) {
            logger.error("Firebase auth error: {}", e.getMessage( ), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        } catch (Exception e) {
            logger.error("Error decoding token: {}", e.getMessage( ), e);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Error processing authentication");
        }
    }

    @Async
    protected void checkAndCreateUser(FirebaseToken decodedToken, String role) {
        String uid = decodedToken.getUid( );
        DocumentReference userRef = firestore.collection("users").document(uid);

        try {
            ApiFuture<DocumentSnapshot> future = userRef.get( );
            DocumentSnapshot document = future.get( );

            if (!document.exists( )) {
                UserModel.UserProfile profile = UserModel.UserProfile.builder( )
                        .college("")
                        .year("")
                        .avatarUrl(decodedToken.getPicture( ) != null ? decodedToken.getPicture( ) : "")
                        .build( );

                UserModel newUser = UserModel.builder( )
                        .id(uid)
                        .email(decodedToken.getEmail( ))
                        .displayName(decodedToken.getName( ))
                        .role(role)
                        .profile(profile)
                        .courseTrack(new HashMap<>( ))
                        .createdAt(Timestamp.now( ))
                        .build( );

                ApiFuture<WriteResult> writeResult = userRef.set(newUser);
                writeResult.get( );
                logger.info("Created new user with UID: {}", uid);
            }
        } catch (Exception e) {
            logger.error("Error in checkAndCreateUser: {}", e.getMessage( ), e);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Error creating user", e);
        }
    }

    public Map<String, Object> requestCreator(String idToken) {
        try {
                FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
                String uid = decodedToken.getUid( );

                // Get current claims in a single operation
                Map<String, Object> claims = decodedToken.getClaims( );
                Object roleObj = claims != null ? claims.get("role") : null;
                String role = roleObj != null ? roleObj.toString( ) : "visitor";

                // Only allow visitors to request creator role
                if (!"visitor".equals(role)) {
                    throw new IllegalStateException("Only users with 'visitor' role can request creator access");
                }

                // Update Firebase Auth claims
                Map<String, Object> newClaims = new HashMap<>();
                newClaims.put("role", "creator");

                // Update Firestore asynchronously
                asyncExecutor.execute(() -> {
                    try {
                       firestoreCreatorUpdate(uid, newClaims);
                    } catch (Exception e) {
                        logger.error("Error updating Firestore: {}", e.getMessage( ), e);
                    }
                });

                // Return success response
                Map<String, Object> response = new HashMap<>( );
                response.put("success", true);
                response.put("message", "Successfully upgraded to creator role");
                response.put("uid", uid);
                response.put("newRole", "creator");

                return response;

            } catch (FirebaseAuthException e) {
                throw new CompletionException("Authentication error: " + e.getMessage( ), e);
            } catch (Exception e) {
                throw new CompletionException("Failed to process creator request: " + e.getMessage( ), e);
            }

    }

    @Async
    private void firestoreCreatorUpdate(String uid, Map<String, Object> newClaims) {
        try {
            firebaseAuth.setCustomUserClaims(uid, newClaims);
            firestore.collection("users").document(uid)
                    .update("role", "creator")
                    .get();
        } catch (ExecutionException e) {
            logger.error("Error executing Firestore update: {}", e.getMessage( ), e);
        } catch (InterruptedException e) {
            logger.error("Error waiting for Firestore update: {}", e.getMessage( ), e);
            Thread.currentThread().interrupt();
        } catch (FirebaseAuthException e) {
            logger.error("Error updating Firebase Auth claims: {}", e.getMessage( ), e);
        }
    }
}