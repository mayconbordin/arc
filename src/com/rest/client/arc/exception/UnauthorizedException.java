package com.rest.client.arc.exception;

import com.rest.client.arc.Response;

public class UnauthorizedException extends ArcException {
	public UnauthorizedException() {
		super("401 Unauthorized");
	}
	
	public UnauthorizedException(Response response) {
		super("401 Unauthorized", response);
	}

	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnauthorizedException(String message) {
		super(message);
	}
	
	public UnauthorizedException(String message, Response response) {
		super(message, response);
	}

	public UnauthorizedException(Throwable cause) {
		super(cause);
	}
}