package com.backend.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChapterRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String markdownUrl;
    @NotBlank
    private String videoUrl;
}
