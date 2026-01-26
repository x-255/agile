package com.perf.backend.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("name")
    private String name;
    
    @TableField("phone")
    private String phone;
    
    @TableField("email")
    private String email;
    
    @TableField("wwUserid")
    private String wwUserid;
    
    @TableField(value = "created_date", fill = FieldFill.INSERT)
    private LocalDateTime createdDate;
    
    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateDate;
    
    @TableLogic
    private Integer deleted;
}