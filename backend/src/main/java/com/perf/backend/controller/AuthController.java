package com.perf.backend.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perf.backend.dto.LoginRequest;
import com.perf.backend.dto.LoginResponse;
import com.perf.backend.dto.RegisterRequest;
import com.perf.backend.dto.Result;
import com.perf.backend.entity.User;
import com.perf.backend.repository.UserRepository;
import com.perf.backend.util.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/auth")
public class AuthController {
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @Autowired
  private JwtUtil jwtUtil;
  
  @PostMapping("/login")
  public Result login(@RequestBody LoginRequest loginRequest) {
    // 查找用户
    User user = userRepository.findByUsername(loginRequest.getUsername());
    if (user == null) {
      return Result.fail(400, "用户名或密码错误");
    }
    
    // 验证密码
    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      return Result.fail(400, "用户名或密码错误");
    }
    
    // 生成JWT token（使用用户ID提高安全性）
    String token = jwtUtil.generateToken(user.getId());
    
    // 创建登录响应（仍然返回用户名用于前端显示）
    LoginResponse loginResponse = new LoginResponse(token, user.getUsername());
    
    return Result.success(loginResponse);
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

  @GetMapping("error")
  public Result error() {
    return Result.fail(401, "错误");
  }

  @GetMapping("test")
  public Result test() {
    return Result.success("test");
  }

}
