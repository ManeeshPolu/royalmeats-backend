package com.royalhalalmeats.royalmeats.controller;

import com.royalhalalmeats.royalmeats.model.User;
import com.royalhalalmeats.royalmeats.repository.UserRepository;
import com.royalhalalmeats.royalmeats.security.JwtUtil;
import com.royalhalalmeats.royalmeats.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository repo;
    private final MailService mailService;

    public UserController(UserRepository repo, MailService mailService) {
        this.repo = repo;
        this.mailService = mailService;
    }

    // 🧾 Signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body("Email already exists");
        }
        return ResponseEntity.ok(repo.save(user));
    }

    // 🔑 Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> u = repo.findByEmail(user.getEmail());
        if (u.isPresent() && u.get().getPassword().equals(user.getPassword())) {
            User existing = u.get();
            String token = JwtUtil.generateToken(existing.getEmail(), existing.getRole());
            return ResponseEntity.ok(Map.of("token", token, "user", existing));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    // 📧 Forgot Password — generates token and emails link
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        Optional<User> optionalUser = repo.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        repo.save(user);

        // 📤 Compose link and send email
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        mailService.sendPasswordResetEmail(user.getEmail(), resetLink);

        return ResponseEntity.ok(Map.of("message", "Reset link sent", "link", resetLink));
    }

    // 🔒 Reset Password — verifies token and sets new password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("password");

        Optional<User> optionalUser = repo.findByResetToken(token);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid token");
        }

        User user = optionalUser.get();

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body("Token expired");
        }

        user.setPassword(newPassword);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        repo.save(user);

        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }

    // 📄 Get user by email
    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return repo.findByEmail(email).orElse(null);
    }
}
