package com.backend.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import com.google.cloud.Timestamp;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class UserModel {
    @DocumentId
    private String id;
    private String email;
    private String displayName;
    private String role;
    private UserProfile profile;
    private Map<String, CourseProgress> courseTrack;

    @ServerTimestamp
    private Timestamp createdAt;

    @Data
    @Builder
    public static class CourseProgress {
        private String completedChapters;
        private String totalChapters;
        private String lastSeenAt;
    }

    @Data
    @Builder
    public static class UserProfile {
        private String college;
        private String year;
        private String avatarUrl;
    }
}