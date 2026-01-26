package com.perf.backend.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String message;
    private Object data;
    private String timestamp;

    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static Result success(Object data) {
        return new Result(200, "success", data);
    }

    public static Result fail(Integer code, String message) {
        return new Result(code, message, null);
    }
}