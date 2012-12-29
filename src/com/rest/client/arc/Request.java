package com.rest.client.arc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.message.BasicNameValuePair;

import com.rest.client.arc.exception.ArcException;

public class Request {
	private Arc client;
	private String resource;
	private Map<String, String> params = new HashMap<String, String>();
    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, ByteArrayBody> byteArrays = new HashMap<String, ByteArrayBody>();;
    
    public Request(String resource) {
		this.resource = resource;
	}
    
    public Request(String resource, Map<String, String> params) {
		this.resource = resource;
		params(params);
	}
    
    public Request(String resource, String[][] params) {
		this.resource = resource;
		params(params);
	}
    
    public Request(String resource, Map<String, String> params, Map<String, String> headers) {
		this.resource = resource;
		params(params);
		headers(headers);
	}
    
    public Request(String resource, String[][] params, String[][] headers) {
		this.resource = resource;
		params(params);
		headers(headers);
	}

	public Request param(String key, String value) {
        params.put(key, value);
        return this;
    }
	
	public Request param(String key, int value) {
		params.put(key, String.valueOf(value));
		return this;
    }
	
	public Request param(String key, double value) {
		params.put(key, String.valueOf(value));
		return this;
    }
	
	public Request params(Map<String, String> params) {
		this.params.putAll(params);
		return this;
	}
	
	public Request params(String[][] params) {
		for (String[] param : params) {
			if (param.length == 2) {
				this.params.put(param[0], param[1]);
			}
		}
		return this;
	}
	
	public Request byteArray(String key, ByteArrayBody value) {
		byteArrays.put(key, value);
		return this;
    }

    public Request header(String key, String value) {
    	params.put(key, value);
    	return this;
    }
    
    public Request headers(Map<String, String> headers) {
		this.headers.putAll(headers);
		return this;
	}
	
	public Request headers(String[][] headers) {
		for (String[] header : headers) {
			if (header.length == 2) {
				this.headers.put(header[0], header[1]);
			}
		}
		return this;
	}

	public String getResource() {
		return resource;
	}

	public Map<String, String> getParams() {
		return params;
	}
	
	public List<NameValuePair> getParamsList() {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for(Map.Entry<String, String> param : params.entrySet())
        	list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
		return list;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
    
	public Map<String, ByteArrayBody> getByteArrays() {
		return byteArrays;
	}
	
	public boolean isMultipart() {
		return byteArrays.size() != 0;
	}

	public void setClient(Arc client) {
		this.client = client;
	}
	
	public Response get() throws IOException, URISyntaxException, ArcException {
		return client.get(this);
	}
	
	public Response head() throws IOException, URISyntaxException, ArcException {
		return client.head(this);
	}
	
	public Response delete() throws IOException, URISyntaxException, ArcException {
		return client.delete(this);
	}
	
	public Response post() throws IOException, URISyntaxException, ArcException {
		if (isMultipart()) {
			return client.postMultipart(this);
		}
		
		return client.post(this);
	}
	
	public Response put() throws IOException, URISyntaxException, ArcException {
		if (isMultipart()) {
			return client.putMultipart(this);
		}
		
		return client.put(this);
	}
}