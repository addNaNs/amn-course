package com.amn.courses.controllers;

import com.amn.courses.dto.CourseRepository;
import com.amn.courses.dto.UserRepository;
import com.amn.courses.models.Course;
import com.amn.courses.models.User;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    String addNewCourse (@RequestBody ObjectNode json) {
        String name = json.get("name").asText();
        Integer instructorId = json.get("instructorId").asInt();
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

    @GetMapping(path="/{id}")
    public @ResponseBody Course getCourse(@PathVariable(value="id") Integer id) {
        return courseRepository.findById(id).get();
    }

    @DeleteMapping(path="{id}")
    public @ResponseBody String deleteCourse(@PathVariable(value="id") Integer id) {
        courseRepository.deleteById(id);
        return "Deleted";
    }

    @PostMapping(path = "/enroll")
    public @ResponseBody String enrollToCourse(@RequestBody ObjectNode json){
        Integer user_id = json.get("user_id").asInt();
        Integer course_id = json.get("course_id").asInt();
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
