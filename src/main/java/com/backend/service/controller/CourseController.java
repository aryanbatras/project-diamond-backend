package com.backend.service.controller;

import com.backend.service.dto.request.ChapterRequest;
import com.backend.service.dto.request.CourseCreateRequest;
import com.backend.service.dto.request.CourseFilterRequest;
import com.backend.service.dto.request.CourseUpdateRequest;
import com.backend.service.model.CourseModel.Chapter;
import com.google.firebase.auth.FirebaseAuthException;
import com.backend.service.business.CourseBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import com.backend.service.model.CourseModel;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseBusiness courseBusiness;

    @Autowired
    public CourseController(CourseBusiness courseBusiness) {
        this.courseBusiness = courseBusiness;
    }

    @GetMapping
    public ResponseEntity<Page<CourseModel>> getAllCourses(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        CourseFilterRequest filter = new CourseFilterRequest();
        filter.setTitle(title);
        filter.setType(type);
        filter.setPage(page);
        filter.setSize(size);
        
        return ResponseEntity.ok(courseBusiness.getAllCourses(filter));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseModel> getCourseById(@PathVariable String courseId) {
        return ResponseEntity.ok(courseBusiness.getCourseById(courseId));
    }
    
    @GetMapping("/creator")
    public ResponseEntity<List<CourseModel>> getCreatorCourses(
            @RequestHeader("Authorization") String authorizationHeader) throws FirebaseAuthException {
        String creatorId = courseBusiness.isCreator(authorizationHeader);
        return ResponseEntity.ok(courseBusiness.getCoursesByCreatorId(creatorId));
    }


    @PostMapping
    public ResponseEntity<CourseModel> createCourse(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody CourseCreateRequest courseCreateRequest
    ) throws FirebaseAuthException {
        String creatorId = courseBusiness.isCreator(authorizationHeader);
        return ResponseEntity.ok(courseBusiness.createCourse(courseCreateRequest, creatorId));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseModel> updateCourse(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String courseId,
            @Valid @RequestBody CourseUpdateRequest updateRequest) throws FirebaseAuthException {
        String updaterId = courseBusiness.isCreator(authorizationHeader);
        return ResponseEntity.ok(courseBusiness.updateCourse(courseId, updateRequest, updaterId));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourseById(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String courseId) throws FirebaseAuthException {
        String requesterId = courseBusiness.isCreator(authorizationHeader);
        courseBusiness.deleteCourseById(courseId, requesterId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{courseId}/chapters")
    public ResponseEntity<Chapter> addChapter(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String courseId,
            @Valid @RequestBody ChapterRequest request) throws FirebaseAuthException {
        String creatorId = courseBusiness.isCreator(authorizationHeader);
        return ResponseEntity.ok(courseBusiness.addChapterToCourse(courseId, request, creatorId));
    }
    
    @PutMapping("/{courseId}/chapters/{chapterId}")
    public ResponseEntity<Chapter> updateChapter(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String courseId,
            @PathVariable String chapterId,
            @Valid @RequestBody ChapterRequest request) throws FirebaseAuthException {
        String updaterId = courseBusiness.isCreator(authorizationHeader);
        return ResponseEntity.ok(
            courseBusiness.updateChapterInCourse(courseId, chapterId, request, updaterId)
        );
    }
}

