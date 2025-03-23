package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Kiểm tra email đã tồn tại chưa
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại!");
        }

        // Mã hóa mật khẩu (nếu có sử dụng bcrypt)
        user.setPassword(user.getPassword()); // Nếu có bcrypt: BCryptPasswordEncoder.encode(user.getPassword())

        // Lưu vào database
        User newUser = userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email không tồn tại!");
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            return ResponseEntity.badRequest().body("Sai mật khẩu!");
        }

        if (user.getBlocked() == 1) {
            return ResponseEntity.badRequest().body("Tài khoản đã bị khóa!");
        }

        if (user.getRole() == User.Role.ADMIN) {
            return ResponseEntity.ok(Map.of("message", "Đăng nhập thành công!", "redirect", "./admin/index.html"));
        } else {
            return ResponseEntity.ok(Map.of("message", "Đăng nhập thành công!", "redirect", "index.html"));
        }
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @PutMapping("/{id}/block")
    public User blockUser(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer blocked = request.get("blocked");
        return userService.blockUser(id, blocked);
    }
}
