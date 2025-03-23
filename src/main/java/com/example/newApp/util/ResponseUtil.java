package com.example.newApp.util;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ResponseUtil {
    private String status;
    private Map<String, Object> data;
    private String error;

    public ResponseUtil() {
        this.data = new HashMap<>();
    }

    public ResponseUtil(String status, Map<String, Object> data, String error) {
        this.status = status;
        this.data = data != null ? data : new HashMap<>();
        this.error = error;
    }
}