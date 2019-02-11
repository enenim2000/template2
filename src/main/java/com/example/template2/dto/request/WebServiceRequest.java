package com.example.template2.dto.request;

import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
public class WebServiceRequest {
    private String request;
    private HttpMethod httpMethod;
    private String url;
}