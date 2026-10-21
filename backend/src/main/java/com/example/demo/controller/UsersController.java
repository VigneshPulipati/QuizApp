package com.example.demo.controller;

import com.example.demo.model.Users;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
@RestController
@RequestMapping("/api/users")
public class UsersController {
    private static final String USER_SESSION_KEY = "authenticatedUser";

    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersController(UserRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Users user) {
        if (user.getEmail() == null || user.getPassword() == null || user.getFullName() == null
                || user.getRole() == null) {
            return ResponseEntity.badRequest().body("All account fields are required.");
        }
        if (usersRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("An account with that email already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.ok(toPublicUser(usersRepository.save(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users loginRequest, HttpSession session) {
        Users user = usersRepository.findByEmail(loginRequest.getEmail());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        }
        session.setAttribute(USER_SESSION_KEY, user);
        return ResponseEntity.ok(toPublicUser(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public List<Users> getAllUsers() {
        return usersRepository.findAll().stream().map(this::toPublicUser).toList();
    }

    public static Users authenticatedUser(HttpSession session) {
        Object user = session.getAttribute(USER_SESSION_KEY);
        return user instanceof Users ? (Users) user : null;
    }

    private Users toPublicUser(Users user) {
        Users result = new Users();
        result.setId(user.getId());
        result.setFullName(user.getFullName());
        result.setRole(user.getRole());
        result.setEmail(user.getEmail());
        return result;
    }
}
