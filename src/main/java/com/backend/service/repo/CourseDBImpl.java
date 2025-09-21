package com.backend.service.repo;

import com.backend.service.model.CourseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseDBImpl implements CourseDBCustom {

    private final MongoTemplate mongoTemplate;

    public CourseDBImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<CourseModel> findAllWithFilters(String title, String type, Pageable pageable) {
        Query query = new Query().with(pageable).with(Sort.by("createdAt").descending());
        
        if (title != null && !title.isBlank()) {
            query.addCriteria(Criteria.where("title").regex(title, "i"));
        }
        
        if (type != null && !type.isBlank()) {
            query.addCriteria(Criteria.where("type").is(type));
        }

        long total = mongoTemplate.count(query, CourseModel.class);
        List<CourseModel> items = mongoTemplate.find(query, CourseModel.class);
        
        return new PageImpl<>(items, pageable, total);
    }
}
