package com.perf.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.perf.backend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // 自定义查询方法
    User findByUsername(String username);
}