package com.example.demo.controller;

import com.example.demo.dto.CreateQuizRequest;
import com.example.demo.dto.QuizResponse;
import com.example.demo.dto.QuizSubmission;
import com.example.demo.dto.ScoreResponse;
import com.example.demo.model.Quiz;
import com.example.demo.model.Users;
import com.example.demo.service.QuizService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.controller.UsersController.authenticatedUser;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<?> createQuiz(@Valid @RequestBody CreateQuizRequest request, HttpSession session) {
        Users user = creator(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only logged-in creators can create quizzes.");
        }
        Quiz saved = quizService.createQuiz(request, user.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.toResponse(saved));
    }

    @GetMapping
    public List<QuizResponse> getAll() {
        return quizService.getAll().stream().map(quizService::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        Quiz quiz = quizService.getById(id);
        return quiz == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(quizService.toResponse(quiz));
    }

    @GetMapping("/domain/{domain}")
    public List<QuizResponse> byDomain(@PathVariable String domain) {
        return quizService.getByDomain(domain).stream().map(quizService::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CreateQuizRequest request,
                                    HttpSession session) {
        Users user = creator(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only logged-in creators can update quizzes.");
        }
        Quiz updated = quizService.updateQuiz(id, request, user.getFullName());
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(quizService.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        Users user = creator(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only logged-in creators can delete quizzes.");
        }
        return quizService.deleteQuiz(id, user.getFullName())
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submit(@PathVariable Long id, @RequestBody QuizSubmission submission,
                                    HttpSession session) {
        Users user = authenticatedUser(session);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in before attempting a quiz.");
        }
        submission.setUsername(user.getFullName());
        ScoreResponse result = quizService.score(id, submission);
        return result.getTotal() == 0 ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/myquizzes/{username}")
    public List<QuizResponse> getMyQuizzes(@PathVariable String username, HttpSession session) {
        Users user = authenticatedUser(session);
        return user == null || !user.getFullName().equals(username) ? List.of() : quizService.getMyQuizzes(username);
    }

    private Users creator(HttpSession session) {
        Users user = authenticatedUser(session);
        return user != null && "creator".equalsIgnoreCase(user.getRole()) ? user : null;
    }
}
