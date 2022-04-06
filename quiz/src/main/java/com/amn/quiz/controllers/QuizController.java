package com.amn.quiz.controllers;

import com.amn.quiz.dto.QuizRepository;
import com.amn.quiz.models.Quiz;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping(path = "/quiz")
public class QuizController {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(path = "")
    public @ResponseBody ResponseEntity<Object> addNewQuiz(@RequestBody Quiz quiz){
        try{
            var r = restTemplate.getForObject(
                    "https://course-app/course/" + quiz.getCourse_id().toString(),
                    String.class
            );
            quizRepository.save(quiz);
        } catch (HttpServerErrorException.InternalServerError e){
            JSONObject entity = new JSONObject();
            entity.put("message","No course with that ID");
            return new ResponseEntity<Object>(entity,HttpStatus.BAD_REQUEST);
        }

        JSONObject entity = new JSONObject();
        entity.put("message","Saved");
        return new ResponseEntity<Object>(entity,HttpStatus.OK);
    }

    @GetMapping(path="")
    public @ResponseBody Iterable<Quiz> getAllUsers() {
        return quizRepository.findAll();
    }

    /*
    @PostMapping(path = "/attempt/{quiz_id}/{user_id}")
    public @ResponseBody String attemptQuiz(
            @PathVariable(value="quiz_id") Integer quiz_id,
            @PathVariable(value="user_id") Integer user_id
    ){
        Quiz quiz = quizRepository.findById(quiz_id).get();
        Integer course_id = quiz.getCourse_id();
        return "Saved";
    }
     */
}
