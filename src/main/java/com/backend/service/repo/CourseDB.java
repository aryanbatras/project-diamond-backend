package com.backend.service.repo;

import com.backend.service.model.CourseModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseDB extends MongoRepository<CourseModel, String> {
}
