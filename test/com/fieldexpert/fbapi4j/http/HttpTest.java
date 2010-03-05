package com.fieldexpert.fbapi4j.http;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fieldexpert.fbapi4j.common.Attachment;
import com.fieldexpert.fbapi4j.http.server.MockServer;

public class HttpTest {

	private static MockServer server;
	private static URL url;
	private Map<String, String> params;

	@Test
	public void get() throws IOException {
		InputStream is = Http.get(url, params);

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		Map<String, String> returnedParams = params(br.readLine());

		assertEquals(params, returnedParams);
	}

	@Test
	public void post() throws IOException {
		InputStream is = Http.post(url, params);

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		Map<String, String> returnedParams = params(br.readLine());

		assertEquals(params, returnedParams);
	}

	@Test
	public void attachments() throws IOException {
		List<Attachment> attachments = asList(new Attachment("foo.txt", "text/plain", "This is my test file."));
		InputStream is = Http.post(url, params, attachments);

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		Map<String, String> returnedParams = params(br.readLine());

		params.put("nFileCount", "1");
		params.put("File1", "This is my test file.");

		assertEquals(params, returnedParams);
	}

	private Map<String, String> params(String response) {
		Map<String, String> result = new HashMap<String, String>();
		String[] params = response.replaceAll("\\{|\\}", "").split(",");
		for (String param : params) {
			String[] tuple = param.split("=");
			result.put(tuple[0].trim(), tuple[1].trim());
		}
		return result;
	}

	@Before
	public void before() {
		params = new HashMap<String, String>();
		params.put("foo", "bar");
		params.put("baz", "buck");
	}

	@BeforeClass
	public static void startServer() {
		String host = "localhost", path = "/echo";
		int port = 9000;
		server = new MockServer(host, port, path);
		server.start();

		try {
			url = new URL("http", host, port, path);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void stopServer() {
		server.stop();
	}
}
