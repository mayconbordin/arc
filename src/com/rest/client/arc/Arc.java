package com.rest.client.arc;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.rest.client.arc.auth.PreemptiveAuthInterceptor;
import com.rest.client.arc.exception.ArcException;
import com.rest.client.arc.exception.UnauthorizedException;
import com.rest.client.arc.gzip.GzipHttpRequestInterceptor;
import com.rest.client.arc.gzip.GzipHttpResponseInterceptor;

public class Arc {
	private static final int CONNECTION_TIMEOUT = 6000;
	private static final int SOCKET_TIMEOUT = 6000;

    private URL baseUrl;
    public AbstractHttpClient httpClient;
    private Map<String, String> globalParams;
    private BasicHttpContext localContext;
    
    public Arc(String baseUrl) throws Exception {
        this.baseUrl = new URL(baseUrl);
        globalParams = new HashMap<String, String>();
        setupHttpClient();
    }
    
	private void setupHttpClient() throws Exception {
    	HttpParams httpParams = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT);
        ConnManagerParams.setMaxTotalConnections(httpParams, 100);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(httpParams, "Arc/0.1");
        
        // Create and initialize scheme registry 
        SchemeRegistry schemeRegistry = new SchemeRegistry();

        // detect https protocol
        if (this.baseUrl.getProtocol().equals("https")) {
	        SSLSocketFactory ssf = new ArcSSLSocketFactory();
	        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        schemeRegistry.register(new Scheme("https", ssf, 443));
        } else {
        	schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        }
        
        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
    	httpClient = new DefaultHttpClient(cm, httpParams);
    	httpClient.addRequestInterceptor(new GzipHttpRequestInterceptor());
        httpClient.addResponseInterceptor(new GzipHttpResponseInterceptor());
    }
    
    public void setBasicAuth(String username, String password) {
    	setBasicAuth(username, password, true);
    }
    
    public void setBasicAuth(String username, String password, boolean preemptive) {
    	httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(username, password)
        );
    	
    	if (preemptive) {
	        localContext = new BasicHttpContext();
	
	        // Generate BASIC scheme object and stick it to the local execution context
	        BasicScheme basicAuth = new BasicScheme();
	        localContext.setAttribute("preemptive-auth", basicAuth);
	
	        // Add as the first request interceptor
	        httpClient.addRequestInterceptor(new PreemptiveAuthInterceptor(), 0);
    	}
    }
    
    public void clearBasicAuth() {
        httpClient.getCredentialsProvider().clear();
    }
    
    
    // begin new interface
    public Request request(String resource) {
    	Request request = new Request(resource);
    	request.setClient(this);
    	return request;
    }
    
    public Request request(String resource, Map<String, String> params) {
    	Request request = new Request(resource, params);
    	request.setClient(this);
    	return request;
    }
    
    public Request request(String resource, String[][] params) {
    	Request request = new Request(resource, params);
    	request.setClient(this);
    	return request;
    }
    
    public Request request(String resource, Map<String, String> params, Map<String, String> headers) {
    	Request request = new Request(resource, params, headers);
    	request.setClient(this);
    	return request;
    }
    
    public Request request(String resource, String[][] params, String[][] headers) {
    	Request request = new Request(resource, params, headers);
    	request.setClient(this);
    	return request;
    }
    // end new interface
    
    
    public Response get(Request req) throws IOException, URISyntaxException, ArcException {
        return request(req, new HttpGet());
    }
    
    public Response head(Request req) throws IOException, URISyntaxException, ArcException {
        return request(req, new HttpHead());
    }
    
    public Response delete(Request req) throws IOException, URISyntaxException, ArcException {
        return request(req, new HttpDelete());
    }
    
    public Response post(Request req) throws IOException, URISyntaxException, ArcException {
    	return entityEnclosingRequest(req, new HttpPost());
    }
    
    public Response put(Request req) throws IOException, URISyntaxException, ArcException {
        return entityEnclosingRequest(req, new HttpPut());
    }
    
    public Response putMultipart(Request req) throws IOException, URISyntaxException, ArcException {
        return multipartRequest(req, new HttpPut());
    }
    
    public Response postMultipart(Request req) throws IOException, URISyntaxException, ArcException {
        return multipartRequest(req, new HttpPost());
    }
    
    @SuppressWarnings("unchecked")
	private Response request(Request r, HttpRequestBase request) throws IOException, URISyntaxException, ArcException {
        String url = baseUrl + r.getResource() + ArcUtils.serializeUrlParams(r.getParams(), globalParams);
    	request.setURI(new URI(url));
    	
        for(Map.Entry<String, String> header : r.getHeaders().entrySet())
        	request.addHeader(header.getKey(), header.getValue());

        return executeRequest(request, url);
    }
    
    @SuppressWarnings("unchecked")
    private Response entityEnclosingRequest(Request r, HttpEntityEnclosingRequestBase request) throws IOException, URISyntaxException, ArcException {
    	String url = baseUrl + r.getResource() + ArcUtils.serializeUrlParams(globalParams);
    	request.setURI(new URI(url));

    	for(Map.Entry<String, String> header : r.getHeaders().entrySet())
        	request.addHeader(header.getKey(), header.getValue());

        if(!r.getParams().isEmpty())
            request.setEntity(new UrlEncodedFormEntity(r.getParamsList(), HTTP.UTF_8));

        return executeRequest(request, url);
    }
    
    @SuppressWarnings("unchecked")
    private Response multipartRequest(Request r, HttpEntityEnclosingRequestBase request) throws IOException, URISyntaxException, ArcException {
    	String url = baseUrl + r.getResource() + ArcUtils.serializeUrlParams(globalParams);
    	request.setURI(new URI(url));
    	    	
    	MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

    	for(Map.Entry<String, String> header : r.getHeaders().entrySet())
        	request.addHeader(header.getKey(), header.getValue());

        if(!r.getParams().isEmpty()) {
        	for (Map.Entry<String, String> entry : r.getParams().entrySet()) {
        		reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue()));
        	}
        }
        
        if(!r.getByteArrays().isEmpty()) {
        	for (Entry<String, ByteArrayBody> entry : r.getByteArrays().entrySet()) {
        		reqEntity.addPart(entry.getKey(), entry.getValue());
        	}
        }
        
        request.setEntity(reqEntity);

        return executeRequest(request, url);
    }

    private Response executeRequest(HttpUriRequest request, String url) throws IOException, ArcException {
    	HttpResponse httpResponse;
    	
    	if (localContext != null) {
        	httpResponse = httpClient.execute(request, localContext);
        } else {
        	httpResponse = httpClient.execute(request);
        }
        
        Response response = new Response(httpResponse);
        response.setUrl(url);

        HttpEntity entity = httpResponse.getEntity();
        
        if (entity != null) {
            InputStream instream = entity.getContent();
            response.setContent(ArcUtils.convertStreamToString(instream));
            
            // Closing the input stream will trigger connection release
            instream.close();
        }
        
        if (response.getCode() == 401) {
        	throw new UnauthorizedException(response);
        }
        
        return response;
    }
    
    protected String buildRequestUrl(Request r) throws UnsupportedEncodingException {
    	return baseUrl.toString() + r.getResource() + ArcUtils.serializeUrlParams(globalParams);
    }

    public void clearGlobalParams() {
    	globalParams = new HashMap<String, String>();
    }
    
    public void addGlobalParam(String name, String value) {
    	globalParams.put(name, value);
    }
    
    public void deleteGlobalParam(String name) {
    	globalParams.remove(name);
    }
}
