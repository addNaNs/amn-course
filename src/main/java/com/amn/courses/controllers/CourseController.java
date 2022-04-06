package com.amn.courses.controllers;

import com.amn.courses.dto.CourseRepository;
import com.amn.courses.dto.UserRepository;
import com.amn.courses.models.Course;
import com.amn.courses.models.User;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping(path="", produces= MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> addNewCourse (@RequestBody ObjectNode json) {
        String name = json.get("name").asText();
        Integer instructorId = json.get("instructorId").asInt();

        if(userRepository.findById(instructorId).isEmpty()){
            JSONObject entity = new JSONObject();
            entity.put("message","No instructor with that ID");
            return new ResponseEntity<Object>(entity,HttpStatus.BAD_REQUEST);
        }
        Course n = new Course();
        n.setName(name);
        n.setInstructor(userRepository.findById(instructorId).get());
        courseRepository.save(n);
        JSONObject entity = new JSONObject();
        entity.put("message","Saved");
        return new ResponseEntity<Object>(entity,HttpStatus.OK);
    }

    @GetMapping(path="")
    public @ResponseBody Iterable<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @DeleteMapping(path="{id}", produces= MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> deleteCourse(@PathVariable(value="id") Integer id) {
        courseRepository.deleteById(id);
        JSONObject entity = new JSONObject();
        entity.put("message","Deleted");
        return new ResponseEntity<Object>(entity,HttpStatus.OK);
    }

    @PostMapping(path = "/enroll", produces= MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> enrollToCourse(@RequestBody ObjectNode json){
        Integer user_id = json.get("user_id").asInt();
        Integer course_id = json.get("course_id").asInt();
        Optional<User> u = userRepository.findById(user_id).stream().findFirst();
        User user;
        if(u.isEmpty()){
            JSONObject entity = new JSONObject();
            entity.put("message","No user with that ID");
            return new ResponseEntity<Object>(entity,HttpStatus.BAD_REQUEST);
        }
        user = u.get();

        Optional<Course> c = courseRepository.findById(course_id).stream().findFirst();
        Course course;
        if(c.isEmpty()){
            JSONObject entity = new JSONObject();
            entity.put("message","No course with that ID");
            return new ResponseEntity<Object>(entity,HttpStatus.BAD_REQUEST);
        }
        course = c.get();

        user.getCourses().add(course);
        userRepository.save(user);
        JSONObject entity = new JSONObject();
        entity.put("message","Enrolled");
        return new ResponseEntity<Object>(entity,HttpStatus.OK);
    }

}
