package com.fieldexpert.fbapi4j.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

class ParameterParser {

	Map<String, String> parse(HttpExchange exchange) throws UnsupportedEncodingException, IOException {
		if ("get".equalsIgnoreCase(exchange.getRequestMethod())) {
			URI requestedUri = exchange.getRequestURI();
			return parseQuery(requestedUri.getRawQuery());
		} else if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
			Headers headers = exchange.getRequestHeaders();
			String contentType = headers.get("Content-Type").get(0);
			if (contentType.startsWith("multipart/form-data")) {
				return parseMultiForm(exchange);
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
				return parseQuery(br.readLine());
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * I should be shot for writing this. It should be noted that this should
	 * never be used for anything important.
	 */
	private Map<String, String> parseMultiForm(HttpExchange exchange) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		String content = string(exchange.getRequestBody());
		String boundary = content.split("\n")[0].split("-------------------------------")[1];
		String[] blocks = content.split(boundary);
		for (String block : blocks) {
			String[] lines = block.split("\n");
			if (lines.length > 4) {
				String name = lines[1].split("name=\"")[1].split("\"")[0];
				if (lines[2].contains("Content-Type")) {
					StringBuilder sb = new StringBuilder();
					for (int i = 4; i < lines.length - 1; i++) {
						sb.append(lines[i].replace("\r", ""));
					}
					params.put(name, sb.toString());
				} else {
					params.put(name, lines[3].replaceAll("\r", "")); // Only works for incredibly simple params
				}
			}
		}
		return params;
	}

	private String string(InputStream is) throws IOException {
		final char[] buffer = new char[0x10000];
		StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(is, "UTF-8");
		int read;
		do {
			read = in.read(buffer, 0, buffer.length);
			if (read > 0) {
				out.append(buffer, 0, read);
			}
		} while (read >= 0);

		return out.toString();
	}

	private Map<String, String> parseQuery(String query) throws UnsupportedEncodingException {
		Map<String, String> params = new HashMap<String, String>();
		if (query != null) {
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				String[] param = pair.split("=");
				String key = URLDecoder.decode(param[0], "utf-8");
				String value = URLDecoder.decode(param[1], "utf-8");
				params.put(key, value);
			}
		}
		return params;
	}
}