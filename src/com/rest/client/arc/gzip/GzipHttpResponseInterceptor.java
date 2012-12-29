package com.rest.client.arc.gzip;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

public class GzipHttpResponseInterceptor implements HttpResponseInterceptor {
    public void process(final HttpResponse response, final HttpContext context) {
        final HttpEntity entity = response.getEntity();
        if (entity != null) {
            final Header encoding = entity.getContentEncoding();
            if (encoding != null) {
                inflateGzip(response, encoding);
            }
        }
    }

    private void inflateGzip(final HttpResponse response, final Header encoding) {
        for (HeaderElement element : encoding.getElements()) {
            if (element.getName().equalsIgnoreCase("gzip")) {
                response.setEntity(new GzipInflatingEntity(response.getEntity()));
                break;
            }
        }
    }
}