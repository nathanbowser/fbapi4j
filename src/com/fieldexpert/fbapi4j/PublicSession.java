package com.fieldexpert.fbapi4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fieldexpert.fbapi4j.http.Http;

class PublicSession implements Session {
	private Configuration config;

	PublicSession(Configuration config) {
		this.config = config;
	}

	public void close() {
		// no-op
	}

	public void close(Case bug) {
		throw new UnsupportedOperationException("Operation not supported since you are not logged in.");
	}

	public void create(Entity t) {
		if (!(t instanceof Case)) {
			throw new UnsupportedOperationException("Only cases can be created using the public api.");
		}
		Case c = (Case) t;
		Map<String, Object> request = c.getFields();
		Map<String, String> parameters;
		if (request != null && !request.isEmpty()) {
			parameters = new HashMap<String, String>(request.size());
			for (Entry<String, Object> e : request.entrySet()) {
				String key = e.getKey();
				Object value = e.getValue();
				parameters.put(key, value == null ? "null" : value.toString());
			}
		} else {
			parameters = Collections.emptyMap();
		}

		parameters.put("pre", "preSubmitBug"); // needed
		parameters.put("fPublic", "1"); // needed

		parameters.put("ixProject", "11"); // must be the id -> Gross
		parameters.put("ixArea", "22"); // must be the id -> Gross

		InputStream is = Http.post(config.getUrl(), parameters);

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void edit(Case bug) {
		throw new UnsupportedOperationException("Operation not supported since you are not logged in.");
	}

	public <T extends Entity> List<T> findAll(Class<T> clazz) {
		throw new UnsupportedOperationException("Operation not supported since you are not logged in.");
	}

	public <T extends Entity> T get(Class<T> clazz, Integer id) {
		throw new UnsupportedOperationException("Operation not supported since you are not logged in.");
	}

	public <T extends Entity> T get(Class<T> clazz, String name) {
		throw new UnsupportedOperationException("Operation not supported since you are not logged in.");
	}

	public List<Case> query(String... criterion) {
		throw new UnsupportedOperationException("Operation not supported since you are not logged in.");
	}

	public void reactivate(Case bug) {
		throw new UnsupportedOperationException("Operation not supported since you are not logged in.");
	}

	public void reopen(Case bug) {
		throw new UnsupportedOperationException("Operation not supported since you are not logged in.");
	}

	public void resolve(Case bug) {
		throw new UnsupportedOperationException("Operation not supported since you are not logged in.");
	}

	public void scout(Case bug) {
		throw new UnsupportedOperationException("Operation not supported since you are not logged in.");
	}
}
