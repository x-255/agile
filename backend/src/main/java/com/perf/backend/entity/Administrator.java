package com.perf.backend.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("administrator")
public class Administrator {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("username")
    private String username;
    
    @TableField("password")
    private String password;
    
    @TableField(value = "created_date", fill = FieldFill.INSERT)
    private LocalDateTime createdDate;
    
    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateDate;
    
    @TableLogic
    private Integer deleted;
}