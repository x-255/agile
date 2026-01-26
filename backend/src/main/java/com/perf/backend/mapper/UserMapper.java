package com.perf.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.perf.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

  @Select("SELECT * FROM user WHERE phone = #{phone} AND name = #{name} AND deleted = 0 LIMIT 1")
  User selectByPhone(@Param("phone") String phone, @Param("name") String name);
}