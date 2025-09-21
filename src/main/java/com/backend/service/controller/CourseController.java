package com.backend.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import com.backend.service.business.CourseBusiness;
import org.springframework.web.bind.annotation.*;
import com.backend.service.model.CourseModel;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    @Autowired
    protected CourseBusiness courseBusiness;

    @GetMapping
    public List<CourseModel> getAllCourses() {
        return courseBusiness.getAllCourses( );
    }

    @GetMapping("/{courseId}")
    public CourseModel getCourseById(@PathVariable String courseId) {
        return courseBusiness.getCourseById(courseId);
    }

    @PostMapping
    public CourseModel createCourse(@RequestBody CourseModel courseModel) {
        return courseBusiness.createCourse(courseModel);
    }

    @PutMapping("/{courseId}")
    public CourseModel updateCourse(@PathVariable String courseId, @RequestBody CourseModel courseModel) {
        return courseBusiness.updateCourse(courseModel);
    }

    @DeleteMapping("/{courseId}")
    public void deleteCourseById(@PathVariable String courseId) {
        courseBusiness.deleteCourseById(courseId);
    }
}

