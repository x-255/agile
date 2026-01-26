package com.perf.backend.service;

import com.perf.backend.entity.User;
import com.perf.backend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserMapper userMapper;

  public UserService(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Transactional
  public User findOrCreateUser(String name, String phone, String email, String identityType, String teamSize, String companySize, String industry, String improvementGoal) {
    User existingUser = userMapper.selectByPhone(phone, name);
    if (existingUser != null) {
      return existingUser;
    }

    User newUser = new User();
    newUser.setName(name);
    newUser.setPhone(phone);
    newUser.setEmail(email);

    userMapper.insert(newUser);
    return newUser;
  }

  public User findById(Integer userId) {
    return userMapper.selectById(userId);
  }
}
