package com.rest.client.arc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.protocol.HTTP;

public class ArcUtils {
	public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            	throw e;
            }
        }
        return sb.toString();
    }
    
    public static String serializeUrlParams(Map<String, String>... allParams) throws UnsupportedEncodingException {
    	Map<String, String> params = new HashMap<String, String>();
    	for (int i=0; i < allParams.length; i++)
    		params.putAll(allParams[i]);
    	
    	String sParams = "";
    	int i = 0;
        for (Map.Entry<String, String> param : params.entrySet()) {
        	sParams += (i == 0) ? "?" : "&";
        	sParams += URLEncoder.encode(param.getKey(), HTTP.UTF_8) + "="
        			+  URLEncoder.encode(param.getValue(), HTTP.UTF_8);
        	i++;
        }
        return sParams;
    }
}
