package com.backend.service.dto.request;

import lombok.Data;
import org.springframework.data.domain.Pageable;

@Data
public class CourseFilterRequest {
    private String title;
    private String type;
    private int page = 0;
    private int size = 10;
}
