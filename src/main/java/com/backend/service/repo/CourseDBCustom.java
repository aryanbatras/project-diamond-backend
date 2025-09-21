package com.backend.service.repo;

import com.backend.service.model.CourseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseDBCustom {
    Page<CourseModel> findAllWithFilters(String title, String type, Pageable pageable);
}
