package com.backend.service.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import com.backend.service.model.CourseModel;
import org.springframework.data.domain.Page;

import java.util.List;

@Repository
public interface CourseDB extends MongoRepository<CourseModel, String>, CourseDBCustom {
    List<CourseModel> findByCreatorId(String creatorId);
}
