package com.example.template2.dto.request;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SimpleRequest extends SuperRequest {
    protected Map<String, Object> data = new HashMap<>();
}