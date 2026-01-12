package com.perf.backend.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("questionnaire_answer_record")
public class QuestionnaireAnswerRecord {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("record_no")
    private String recordNo;
    
    @TableField("user_id")
    private Integer userId;
    
    @TableField("questionnaire_json")
    private String questionnaireJson;
    
    @TableField("start_time")
    private LocalDateTime startTime;
    
    @TableField("end_time")
    private LocalDateTime endTime;
    
    @TableField(value = "created_date", fill = FieldFill.INSERT)
    private LocalDateTime createdDate;
    
    @TableLogic
    private Integer deleted;
}