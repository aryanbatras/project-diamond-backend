package com.backend.service.business;

import com.backend.service.dto.request.ChapterRequest;
import com.backend.service.dto.request.CourseCreateRequest;
import com.backend.service.dto.request.CourseFilterRequest;
import com.backend.service.dto.request.CourseUpdateRequest;
import com.backend.service.model.CourseModel;
import com.backend.service.repo.CourseDB;
import com.backend.service.util.AuthUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.ArrayList;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class CourseBusiness {

    @Autowired
    private CourseDB courseDB;

    @Autowired
    FirebaseAuth firebaseAuth;

    public List<CourseModel> getCoursesByCreatorId(String creatorId) {
        List<CourseModel> courses = courseDB.findByCreatorId(creatorId);
        if (courses.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No courses found by creator ID " + creatorId);
        return courses;
    }

    public CourseModel createCourse(CourseCreateRequest courseCreateRequest, String creatorId) {
        CourseModel course = CourseModel.builder()
                .type(courseCreateRequest.getType())
                .title(courseCreateRequest.getTitle())
                .description(courseCreateRequest.getDescription())
                .imageUrl(courseCreateRequest.getImageUrl())
                .trailerUrl(courseCreateRequest.getTrailerUrl())
                .creatorId(creatorId)
                .visibility("PRIVATE")
                .publishStatus("DRAFT")
                .createdAt(Date.from(Instant.now()))
                .updatedAt(Date.from(Instant.now()))
                .build();
        return courseDB.save(course);
    }

    public CourseModel updateCourse(String courseId, CourseUpdateRequest updateRequest, String updaterId) {

        CourseModel existingCourse = courseDB.findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        
        if (!existingCourse.getCreatorId().equals(updaterId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the course creator can update this course");

        if (updateRequest.getType() != null) existingCourse.setType(updateRequest.getType());
        if (updateRequest.getTitle() != null) existingCourse.setTitle(updateRequest.getTitle());
        if (updateRequest.getDescription() != null) existingCourse.setDescription(updateRequest.getDescription());
        if (updateRequest.getImageUrl() != null) existingCourse.setImageUrl(updateRequest.getImageUrl());
        if (updateRequest.getTrailerUrl() != null) existingCourse.setTrailerUrl(updateRequest.getTrailerUrl());
        existingCourse.setUpdatedAt(Date.from(Instant.now()));
        
        return courseDB.save(existingCourse);
    }

    public void deleteCourseById(String courseId, String requesterId) {
        CourseModel course = courseDB.findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
            
        if (!course.getCreatorId().equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the course creator can delete this course");
        }
        
        if (course.getChapters() != null && !course.getChapters().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete course with existing chapters. Please delete all chapters first.");
        }
        
        courseDB.deleteById(courseId);
    }
    
    public CourseModel.Chapter addChapterToCourse(String courseId, ChapterRequest request, String creatorId) {
        CourseModel course = courseDB.findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        
        if (!course.getCreatorId().equals(creatorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the course creator can add chapters");
        }

        CourseModel.Chapter chapter = new CourseModel.Chapter();
        chapter.setChapterId(UUID.randomUUID().toString());
        chapter.setTitle(request.getTitle());
        chapter.setMarkdownUrl(request.getMarkdownUrl());
        chapter.setVideoUrl(request.getVideoUrl());

        if (course.getChapters() == null) {
            course.setChapters(new ArrayList<>());
        }
        course.getChapters().add(chapter);
        courseDB.save(course);
        
        return chapter;
    }

    public CourseModel.Chapter updateChapterInCourse(String courseId, String chapterId, ChapterRequest request, String updaterId) {
        CourseModel course = courseDB.findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        
        if (!course.getCreatorId().equals(updaterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the course creator can update chapters");
        }

        return course.getChapters().stream()
            .filter(ch -> ch.getChapterId().equals(chapterId))
            .findFirst()
            .map(chapter -> {
                if (request.getTitle() != null) chapter.setTitle(request.getTitle());
                if (request.getMarkdownUrl() != null) chapter.setMarkdownUrl(request.getMarkdownUrl());
                if (request.getVideoUrl() != null) chapter.setVideoUrl(request.getVideoUrl());
                courseDB.save(course);
                return chapter;
            })
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter not found"));
    }

    // AUTHORIZATION
    public String isCreator(String authorizationHeader) {
        String token = AuthUtils.extractBearerToken(authorizationHeader);
        FirebaseToken decodedToken;
        try {
            decodedToken = firebaseAuth.verifyIdToken(token);
        } catch (FirebaseAuthException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Firebase token", ex);
        }
        String uid = decodedToken.getUid();
        String role = AuthUtils.extractRoleFromToken(decodedToken);

        if (!"creator".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only creators can access this endpoint");
        }
        return uid;
    }

    // GENERAL ENDPOINT
    public CourseModel getCourseById(String courseId) throws ResponseStatusException {
        return courseDB.findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    public Page<CourseModel> getAllCourses(CourseFilterRequest filter) {
        int page = Math.max(0, filter.getPage());
        int size = filter.getSize() <= 0 ? 10 : Math.min(filter.getSize(), 100);
        
        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("createdAt").descending()
        );
        
        String title = (filter.getTitle() != null && !filter.getTitle().isBlank()) ? filter.getTitle() : null;
        String type = (filter.getType() != null && !filter.getType().isBlank()) ? filter.getType() : null;
        
        return courseDB.findAllWithFilters(title, type, pageable);
    }

}

