package com.perf.backend.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("report")
public class Report {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("user_id")
    private Integer userId;
    
    @TableField("record_id")
    private Integer recordId;
    
    @TableField("comprehensive_data")
    private String comprehensiveData;
    
    @TableField("dimension_data")
    private String dimensionData;
    
    @TableField("implementation_data")
    private String implementationData;
    
    @TableField(value = "created_date", fill = FieldFill.INSERT)
    private LocalDateTime createdDate;
    
    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateDate;
    
    @TableLogic
    private Integer deleted;
}