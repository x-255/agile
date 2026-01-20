package com.perf.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.perf.backend.dto.LoginRequest;
import com.perf.backend.dto.LoginResponse;
import com.perf.backend.dto.RegisterRequest;
import com.perf.backend.dto.Result;
import com.perf.backend.entity.Administrator;
import com.perf.backend.service.AdministratorService;
import com.perf.backend.util.JwtUtil;
import com.perf.backend.util.WeChatWorkUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {
  
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  private AdministratorService administratorService;
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private WeChatWorkUtil weChatWorkUtil;

  @PostMapping("/login")
  public Result login(@RequestBody LoginRequest loginRequest) {
    // 查找管理员
    Administrator administrator = administratorService.findByUsername(loginRequest.getUsername());
    if (administrator == null) {
      return Result.fail(400, "用户名或密码错误");
    }
    
    // 验证密码
    if (!passwordEncoder.matches(loginRequest.getPassword(), administrator.getPassword())) {
      return Result.fail(400, "用户名或密码错误");
    }
    
    // 生成JWT token（使用管理员ID提高安全性）
    String token = jwtUtil.generateToken(administrator.getId());
    
    // 创建登录响应（仍然返回用户名用于前端显示）
    LoginResponse loginResponse = new LoginResponse(token, administrator.getUsername());
    
    return Result.success(loginResponse);
  }
  
  @PostMapping("/register")
  public Result register(@RequestBody RegisterRequest registerRequest) {
    // 检查用户名是否已存在
    if (administratorService.findByUsername(registerRequest.getUsername()) != null) {
      return Result.fail(400, "用户名已存在");
    }
    
    // 创建新管理员
    Administrator administrator = new Administrator();
    administrator.setUsername(registerRequest.getUsername());
    // 加密密码
    administrator.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    
    // 保存管理员
    boolean success = administratorService.save(administrator);
    if (!success) {
      return Result.fail(500, "注册失败");
    }
    
    return Result.success(null);
  }

  @PostMapping("error")
  public Result error() {
    return Result.fail(401, "错误1111");
  }

  @GetMapping("test")
  public Result test() {
    // return Result.fail(400, "错误1111");
    return Result.success("test");
  }

  @GetMapping("wechat-callback")
  public Result weChatCallback(@RequestParam String code) {
    try {
      String accessToken = weChatWorkUtil.getAccessToken();
      java.util.Map<String, Object> basicInfo = weChatWorkUtil.getUserBasicInfo(accessToken, code);
      
      // 打印basicInfo，方便调试
      logger.info("BasicInfo: {}", basicInfo);
      
      java.util.Map<String, Object> userInfo;
      // 检查可能的字段名，包括大小写变体
      if (basicInfo.containsKey("user_ticket") || basicInfo.containsKey("userTicket")) {
        String userTicket = (String) (basicInfo.get("user_ticket") != null ? basicInfo.get("user_ticket") : basicInfo.get("userTicket"));
        userInfo = weChatWorkUtil.getUserInfoByTicket(accessToken, userTicket);

        // 如果获取到了userid，调用user/get接口获取用户名
        if (userInfo != null && userInfo.containsKey("userid")) {
          String userId = (String) userInfo.get("userid");
          java.util.Map<String, Object> userBasicInfo = weChatWorkUtil.getUserInfoById(accessToken, userId);
          // 合并用户名到userInfo中
          if (userBasicInfo.containsKey("name")) {
            userInfo.put("name", userBasicInfo.get("name"));
          }
        }
      } else if (basicInfo.containsKey("UserId") || basicInfo.containsKey("userid") || basicInfo.containsKey("userId")) {
        String userId = (String) (
            basicInfo.get("UserId") != null ? basicInfo.get("UserId") : 
            basicInfo.get("userid") != null ? basicInfo.get("userid") : 
            basicInfo.get("userId")
        );
        userInfo = weChatWorkUtil.getUserInfoById(accessToken, userId);
      } else {
        // 返回basicInfo以便调试
        throw new RuntimeException("No valid user information found. BasicInfo: " + basicInfo);
      }
      
      // 打印userInfo，方便调试
      logger.info("UserInfo: {}", userInfo);

      return Result.success(userInfo);
    } catch (Exception e) {
      return Result.fail(500, "企业微信登录失败：" + e.getMessage());
    }
  }

  @GetMapping("wecom-signature")
  public Result getWecomSignature(@RequestParam String url) {
    try {
      java.util.Map<String, String> signature = weChatWorkUtil.getJsApiSignature(url);
      return Result.success(signature);
    } catch (Exception e) {
      logger.error("获取企业微信签名失败", e);
      return Result.fail(500, "获取企业微信签名失败：" + e.getMessage());
    }
  }

}
