package com.rest.client.arc.auth;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

public class PreemptiveAuthInterceptor implements HttpRequestInterceptor {
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {

        AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

        // If no auth scheme available yet, try to initialize it preemptively
        if (authState.getAuthScheme() == null) {
            AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                    ClientContext.CREDS_PROVIDER);
            HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
            if (authScheme != null) {
                Credentials creds = credsProvider.getCredentials(
                        new AuthScope(targetHost.getHostName(), targetHost.getPort()));
                if (creds == null) {
                    throw new HttpException("No credentials for preemptive authentication");
                }
                authState.setAuthScheme(authScheme);
                authState.setCredentials(creds);
            }
        }
    }
}