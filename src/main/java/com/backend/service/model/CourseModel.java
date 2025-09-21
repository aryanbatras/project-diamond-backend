package com.backend.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Document(collection = "courses")
public class CourseModel {
    @Id
    @JsonProperty("courseId")
    private String courseId;

    @JsonProperty("type")
    private String type;
    
    @JsonProperty("title")
    private String title;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("description")
    private String description;

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("trailerUrl")
    private String trailerUrl;

    @JsonProperty("creatorId")
    private String creatorId;

    @JsonProperty("creatorSnapshot")
    private CreatorSnapshot creatorSnapshot;

    @JsonProperty("visibility")
    private String visibility;

    @JsonProperty("publishStatus")
    private String publishStatus;

    @JsonProperty("createdAt")
    private Date createdAt;

    @JsonProperty("updatedAt")
    private Date updatedAt;

    @JsonProperty("chapters")
    private List<Chapter> chapters;

    @Getter
    @Setter
    public static class CreatorSnapshot {
        @JsonProperty("uid")
        private String uid;

        @JsonProperty("displayName")
        private String displayName;

        @JsonProperty("avatarUrl")
        private String avatarUrl;
    }

    @Getter
    @Setter
    public static class Chapter {
        @JsonProperty("chapterId")
        private String chapterId;

        @JsonProperty("title")
        private String title;

        @JsonProperty("markdownUrl")
        private String markdownUrl;

        @JsonProperty("videoUrl")
        private String videoUrl;
    }
}