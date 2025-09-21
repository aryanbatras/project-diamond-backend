package com.backend.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseUpdateRequest {
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;
    
    @NotBlank(message = "Image URL is required")
    private String imageUrl;
    
    private String trailerUrl;
}
