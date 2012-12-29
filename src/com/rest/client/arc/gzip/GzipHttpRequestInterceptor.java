package com.rest.client.arc.gzip;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class GzipHttpRequestInterceptor implements HttpRequestInterceptor {
    public void process(final HttpRequest request, final HttpContext context) {
        if (!request.containsHeader("Accept-Encoding")) {
            request.addHeader("Accept-Encoding", "gzip");
        }
    }
}