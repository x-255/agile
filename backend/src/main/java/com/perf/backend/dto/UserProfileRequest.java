package com.perf.backend.dto;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String identityType;
    private String industry;
    private String[] improvementGoal; // 支持数组类型
    private Integer length; // 问卷长度，默认为15
}