package com.perf.backend.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perf.backend.dto.RegisterRequest;
import com.perf.backend.dto.Result;
import com.perf.backend.entity.User;
import com.perf.backend.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @GetMapping("/login")
  public String login() {
    return "login";
  }
  
  @PostMapping("/register")
  public Result register(@RequestBody RegisterRequest registerRequest) {
    // 检查用户名是否已存在
    if (userRepository.findByUsername(registerRequest.getUsername()) != null) {
      return Result.fail(400, "用户名已存在");
    }
    
    // 创建新用户
    User user = new User();
    user.setUsername(registerRequest.getUsername());
    // 加密密码
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    user.setCreatedDate(LocalDateTime.now());
    user.setUpdateDate(LocalDateTime.now());
    
    // 保存用户
    userRepository.save(user);
    
    return Result.success(null);
  }
}
