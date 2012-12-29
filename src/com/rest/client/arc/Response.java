package com.rest.client.arc;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class Response {
	public HttpResponse httpResponse;
	private String url;
	private int code;
	private String message;
	private String content;
	private Map<String, String> headers;
	
	public Response(){}
	public Response(HttpResponse response) {
		this.httpResponse = response;
		this.message = response.getStatusLine().getReasonPhrase();
        this.code = response.getStatusLine().getStatusCode();
        
        headers = new HashMap<String, String>();
        
        for (Header header : response.getAllHeaders()) {
        	headers.put(header.getName(), header.getValue());
        }
	}
    
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int responseCode) {
		this.code = responseCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String response) {
		this.content = response;
	}
	
	public String getHeader(String name) {
		return headers.get(name);
	}
}