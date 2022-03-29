package com.amn.courses.controllers;

import com.amn.courses.dto.CourseRepository;
import com.amn.courses.dto.UserRepository;
import com.amn.courses.models.Course;
import com.amn.courses.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping(path = "/course")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path="")
    public @ResponseBody
    String addNewCourse (
            @RequestParam String name,
            @RequestParam Integer instructorId
    ) {
        Course n = new Course();
        n.setName(name);
        n.setInstructor(userRepository.findById(instructorId).get());

        if(userRepository.findById(instructorId).isEmpty()){
            ResponseEntity.status(HttpStatus.BAD_REQUEST);
            return "No instructor with that ID";
        }

        courseRepository.save(n);
        return "Saved";
    }

    @GetMapping(path="")
    public @ResponseBody Iterable<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @DeleteMapping(path="{id}")
    public @ResponseBody String deleteCourse(@PathVariable(value="id") Integer id) {
        courseRepository.deleteById(id);
        return "Deleted";
    }

    @PostMapping(path = "/enroll")
    public @ResponseBody String enrollToCourse(
            @RequestParam Integer user_id,
            @RequestParam Integer course_id
    ){
        Optional<User> u = userRepository.findById(user_id).stream().findFirst();
        User user;
        if(u.isEmpty()){
            ResponseEntity.status(HttpStatus.BAD_REQUEST);
            return "No user with that ID";
        }
        user = u.get();

        Optional<Course> c = courseRepository.findById(course_id).stream().findFirst();
        Course course;
        if(c.isEmpty()){
            ResponseEntity.status(HttpStatus.BAD_REQUEST);
            return "No course with that ID";
        }
        course = c.get();

        user.getCourses().add(course);
        userRepository.save(user);
        return "Enrolled";
    }

}
