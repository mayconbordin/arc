package com.rest.client.arc.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import com.rest.client.arc.Arc;
import com.rest.client.arc.Response;
import com.rest.client.arc.exception.ArcException;
import com.rest.client.arc.test.util.Config;

public class ArcTest {
	private Arc arc;
	
	@Before
	public void setUp() throws Exception {
		Config cfg = new Config("src/com/rest/client/arc/test/config.properties");
		System.out.println(cfg.getProperty("GitHubUser"));
		
		arc = new Arc("https://api.github.com");
		arc.setBasicAuth("mayconbordin", "o9i2j7h6MVB77:GITHUB#");
	}

	@Test
	public void testGet() {
		try {
			Response resp = arc.request("/users/joyent/repos")
							   .param("page", 1)
							   .param("per_page", 10)
							   .param("callback", "foo")
							   .get();
			
			
			assertEquals(resp.getCode(), 200);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ArcException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetStarredRepo() {
		Response resp = null;
		
		try {
			resp = arc.request("/user/starred/joyent/node")
					  .get();

			assertEquals(resp.getCode(), 404);
		} catch (ArcException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPutStarRepo() {
		Response resp = null;
		
		try {
			resp = arc.request("/user/starred/documentcloud/backbone")
					  .put();

			assertEquals(resp.getCode(), 204);
		} catch (ArcException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDeleteUnstarRepo() {
		Response resp = null;
		
		try {
			resp = arc.request("/user/starred/documentcloud/backbone")
					  .delete();

			assertEquals(resp.getCode(), 204);
		} catch (ArcException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
