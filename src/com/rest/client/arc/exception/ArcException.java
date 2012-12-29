package com.rest.client.arc.exception;

import com.rest.client.arc.Response;

public class ArcException extends Exception {
	private Response response;
	
	public ArcException() {
		super();
	}

	public ArcException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArcException(String message) {
		super(message);
	}
	
	public ArcException(String message, Response response) {
		super(message);
		this.response = response;
	}

	public ArcException(Throwable cause) {
		super(cause);
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
}