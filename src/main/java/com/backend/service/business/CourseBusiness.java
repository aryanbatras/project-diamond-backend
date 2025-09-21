package com.backend.service.business;

import com.backend.service.model.CourseModel;
import com.backend.service.repo.CourseDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseBusiness {

    @Autowired
    private CourseDB courseDB;

    public List<CourseModel> getAllCourses() {
        return courseDB.findAll( );
    }

    public CourseModel getCourseById(String courseId) {
        return courseDB.findById(courseId).orElse(null);
    }

    public CourseModel createCourse(CourseModel course) {
        return courseDB.save(course);
    }

    public CourseModel updateCourse(CourseModel course) {
        return courseDB.save(course);
    }

    public void deleteCourseById(String courseId) {
        courseDB.deleteById(courseId);
    }
}
