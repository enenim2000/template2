package com.example.template2.dto.request;

import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
public class SuperRequest{

	protected String auth;
    protected String login_id;
	protected String user_agent;
	protected HttpMethod _method;
	protected String paginate;

}
