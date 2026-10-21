package com.example.demo.controller;

import com.example.demo.dto.QuizResultDTO;
import com.example.demo.model.Users;
import com.example.demo.repository.QuizResultRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
@RestController
@RequestMapping("/api/results")
public class ResultsController {
    private final QuizResultRepository quizResultRepository;

    public ResultsController(QuizResultRepository quizResultRepository) {
        this.quizResultRepository = quizResultRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getResultsByUsername(HttpSession session) {
        Users user = UsersController.authenticatedUser(session);
        return user == null
                ? ResponseEntity.status(401).body("Please log in to view results.")
                : ResponseEntity.ok(quizResultRepository.findResultsByUsername(user.getFullName()));
    }

    @GetMapping("/quiz/{quizId}")
    public List<QuizResultDTO> getResultsByQuiz(@PathVariable Long quizId) {
        return quizResultRepository.findResultsByQuizId(quizId);
    }

    @GetMapping
    public List<QuizResultDTO> getAllResults() {
        return quizResultRepository.findAllResultsWithQuiz();
    }
}
