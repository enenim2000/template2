package com.example.template2.service;

import com.example.template2.dto.request.WebServiceRequest;
import com.example.template2.util.CommonUtil;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RestClientService {

	private static final Logger logger = LoggerFactory.getLogger(RestClientService.class);

	@Value("${api_key}")
    private String apiKey;

	@Value("${verify_certificate}")
	private String verifyCertificate;

	private RestTemplate restTemplate()
			throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
				.loadTrustMaterial(null, acceptingTrustStrategy)
				.build();

		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLSocketFactory(csf)
				.build();

		HttpComponentsClientHttpRequestFactory requestFactory =
				new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);
		return new RestTemplate(requestFactory);
	}

	private RestTemplate restTemplateHttpsResolver(){
		RestTemplate restTemplate = null;
		if("true".equalsIgnoreCase(verifyCertificate)){
			restTemplate = new RestTemplate();
		}else {
			try {
				restTemplate = restTemplate();
			}catch ( KeyStoreException | NoSuchAlgorithmException | KeyManagementException e){
				e.printStackTrace();
			}
		}

		if(restTemplate == null)
			restTemplate = new RestTemplate();

		return restTemplate;
	}

	public String connect(WebServiceRequest requestBody){
		return consumeWebServiceCore(requestBody.getRequest(), requestBody.getHttpMethod(), requestBody.getUrl());
	}

	private String consumeWebServiceCore(String request, HttpMethod httpMethod, String url) {

		RestTemplate restTemplate = restTemplateHttpsResolver();

		setTimeout(restTemplate);

		ResponseEntity<String> response = null;

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("api-key", apiKey);

		try {
			HttpEntity<String> entity = new HttpEntity<>(request, headers);
			if(httpMethod == HttpMethod.GET){
				url += convertStringRequestToQueryParam(request);
			}
			response = restTemplate.exchange(url, httpMethod, entity, String.class);

		}catch (HttpStatusCodeException exception) {
			exception.printStackTrace();
		}

		return response.getBody();
	}

	public Object consumeWebService(Object request, Class<?> responseType, HttpMethod httpMethod, String url){
		String jsonRequest = CommonUtil.getGson().toJson(request);
		String response = consumeWebServiceCore(jsonRequest, httpMethod,url);
		return CommonUtil.getGson().fromJson(response, responseType);
	}

	private String convertStringRequestToQueryParam(String stringRequest){
		return stringRequest;
	}

	private void setTimeout(RestTemplate restTemplate) {
		int readTimeout = 5 * 60 * 1000;
		int connectionTimeout = 2 * 60 * 1000;
		ClientHttpRequestFactory factory = restTemplate.getRequestFactory();

		if ( factory instanceof SimpleClientHttpRequestFactory)
		{
			((SimpleClientHttpRequestFactory) factory).setConnectTimeout( connectionTimeout );
			((SimpleClientHttpRequestFactory) factory).setReadTimeout( readTimeout );
		}
		else if ( factory instanceof HttpComponentsClientHttpRequestFactory) {
			((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout( readTimeout );
			((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout( connectionTimeout );
		}

		restTemplate.setRequestFactory( factory );
	}
}