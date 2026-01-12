package com.perf.backend.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("dictionary")
public class Dictionary {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("category")
    private String category;
    
    @TableField("code")
    private String code;
    
    @TableField("name")
    private String name;
    
    @TableField("description")
    private String description;
    
    @TableField("`order`")
    private Integer order;
    
    @TableField(value = "created_date", fill = FieldFill.INSERT)
    private LocalDateTime createdDate;
    
    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateDate;
}