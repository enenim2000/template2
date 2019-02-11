package com.example.template2.controller;

import com.example.template2.dto.request.SimpleRequest;
import com.example.template2.dto.request.WebServiceRequest;
import com.example.template2.service.RestClientService;
import com.example.template2.util.CommonUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/web-services")
public class RestController{

    @Value("${api_key}")
    private String apiKey;

    @Value("api_base_url")
    private String webServiceBaseUrl;

    private final RestClientService restClientService;

    @Autowired
    public RestController(RestClientService restClientService) {
        this.restClientService = restClientService;
    }


    @PostMapping(value = "/get", produces = "Application/json")
    @ResponseBody
    public String getEndpoint(String paramString, String url, String page){
        WebServiceRequest webServiceRequest = formatRequest(url+"?page="+page, paramString, HttpMethod.GET);
        String response = restClientService.connect(webServiceRequest);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("feedback", response);
        return new Gson().toJson( dataMap );
    }

    @PostMapping(value = "/create", produces = "Application/json")
    @ResponseBody
    public String createEndpoint(String paramString, String url){
        WebServiceRequest webServiceRequest;
        webServiceRequest = formatRequest(url, paramString, HttpMethod.POST);
        String response = restClientService.connect(webServiceRequest);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("feedback", response);
        return new Gson().toJson( dataMap );
    }

    @PostMapping(value = "/update", produces = "Application/json")
    @ResponseBody
    public String updateEndpoint(String paramString, String url){
        WebServiceRequest webServiceRequest = formatRequest(url, paramString, HttpMethod.PUT);
        String response = restClientService.connect(webServiceRequest);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("feedback", response);
        return new Gson().toJson( dataMap );
    }

    @PostMapping(value = "/delete", produces = "Application/json")
    @ResponseBody
    public String deleteEndpoint(String paramString, String url){
        WebServiceRequest webServiceRequest = formatRequest(url, paramString, HttpMethod.DELETE);
        String response = restClientService.connect(webServiceRequest);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("feedback", response);
        return new Gson().toJson( dataMap );
    }

    private WebServiceRequest formatRequest(String url, String paramString, HttpMethod httpMethod){
        SimpleRequest request = CommonUtil.getGson().fromJson(paramString, SimpleRequest.class);
        request.set_method(httpMethod);
        WebServiceRequest webServiceRequest = new WebServiceRequest();
        webServiceRequest.setRequest( CommonUtil.getGson().toJson(request) );
        url = webServiceBaseUrl + url;
        webServiceRequest.setUrl(url);
        webServiceRequest.setHttpMethod(httpMethod);
        return webServiceRequest;
    }
}