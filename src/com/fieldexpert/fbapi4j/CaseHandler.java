package com.fieldexpert.fbapi4j;

import static com.fieldexpert.fbapi4j.common.StringUtil.collectionToCommaDelimitedString;
import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fieldexpert.fbapi4j.common.Assert;
import com.fieldexpert.fbapi4j.common.Attachment;
import com.fieldexpert.fbapi4j.common.StringUtil;
import com.fieldexpert.fbapi4j.common.Util;
import com.fieldexpert.fbapi4j.dispatch.Dispatch;
import com.fieldexpert.fbapi4j.dispatch.Request;
import com.fieldexpert.fbapi4j.dispatch.Response;

class CaseHandler extends AbstractHandler<Case> {

	private static final String cols = collectionToCommaDelimitedString(asList(Fbapi4j.S_PROJECT, Fbapi4j.S_AREA, Fbapi4j.S_SCOUT_DESCRIPTION, Fbapi4j.S_TITLE, Fbapi4j.S_EVENT, Fbapi4j.EVENTS));

	CaseHandler(Dispatch dispatch, Util util, String token) {
		super(dispatch, util, token);
	}

	private void allowed(Case c, AllowedOperation operation) {
		if (!c.getAllowedOperations().contains(operation)) {
			throw new Fbapi4jException("Operation " + operation.toString() + " is not allowed. [Allowed operations: " + c.getAllowedOperations() + "].");
		}
	}

	@Override
	Case build(Map<String, String> data) {
		// TODO
		return null;
	}

	public void close(Case bug) {
		allowed(bug, AllowedOperation.CLOSE);
		Response resp = send(Fbapi4j.CLOSE, util.map(Fbapi4j.IX_BUG, bug.getId()));
		updateCase(bug, util.data(resp.getDocument(), "case").get(0));
	}

	public void create(Case t) {
		Case bug = (Case) t;
		List<Attachment> attachments = bug.getAttachments();
		Map<String, Object> parameters = events(bug);
		Response resp;

		if (attachments == null) {
			resp = send(Fbapi4j.NEW, parameters);
		} else {
			resp = send(Fbapi4j.NEW, parameters, attachments);
		}

		updateCase(bug, util.data(resp.getDocument(), "case").get(0));
	}

	public void edit(Case bug) {
		Assert.notNull(bug.getId());
		allowed(bug, AllowedOperation.EDIT);
		Response resp = send(Fbapi4j.EDIT, events(bug));
		updateCase(bug, util.data(resp.getDocument(), "case").get(0));
	}

	private Map<String, Object> events(Case c) {
		return new HashMap<String, Object>(c.getFields());
	}

	public List<Case> findAll() {
		return query();
	}

	public Case findById(Integer id) {
		Response resp = dispatch.invoke(new Request(Fbapi4j.SEARCH, util.map(Fbapi4j.TOKEN, token, Fbapi4j.QUERY, id, Fbapi4j.COLS, cols)));
		return new CaseBuilder(util, dispatch.getEndpoint(), token).singleResult(resp.getDocument());
	}

	public Case findByName(String name) {
		throw new UnsupportedOperationException("Not *yet* supported");
	}

	public List<Case> query(String... criterion) {
		String q = collectionToCommaDelimitedString(asList(criterion));
		Response resp = dispatch.invoke(new Request(Fbapi4j.SEARCH, util.map(Fbapi4j.TOKEN, token, Fbapi4j.COLS, cols, Fbapi4j.QUERY, q)));
		return new CaseBuilder(util, dispatch.getEndpoint(), token).list(resp.getDocument());
	}

	public void reactivate(Case bug) {
		Assert.notNull(bug.getId());
		allowed(bug, AllowedOperation.REACTIVATE);
		Response resp = send(Fbapi4j.REACTIVATE, events(bug));
		updateCase(bug, util.data(resp.getDocument(), "case").get(0));
	}

	public void reopen(Case bug) {
		Assert.notNull(bug.getId());
		allowed(bug, AllowedOperation.REOPEN);
		Response resp = send(Fbapi4j.REOPEN, events(bug));
		updateCase(bug, util.data(resp.getDocument(), "case").get(0));
	}

	public void resolve(Case bug) {
		Assert.notNull(bug.getId());
		allowed(bug, AllowedOperation.RESOLVE);
		Response resp = send(Fbapi4j.RESOLVE, events(bug));
		updateCase(bug, util.data(resp.getDocument(), "case").get(0));
	}

	void scout(Case bug) {
		Assert.notNull(token);
		List<Attachment> attachments = bug.getAttachments();
		Map<String, Object> parameters = events(bug);
		parameters.put(Fbapi4j.S_SCOUT_DESCRIPTION, bug.getTitle());

		if (attachments == null) {
			send(Fbapi4j.NEW, parameters);
		} else {
			send(Fbapi4j.NEW, parameters, attachments);
		}
	}

	private Response send(String command, Map<String, Object> parameters) {
		return send(command, parameters, null);
	}

	private Response send(String command, Map<String, Object> parameters, List<Attachment> attachments) {
		parameters.put(Fbapi4j.TOKEN, token);

		Request request = new Request(command, parameters);
		if (attachments != null) {
			request.attach(attachments);
		}
		return dispatch.invoke(request);
	}

	private void updateCase(Case c, Map<String, String> data) {
		c.setId(Integer.parseInt(data.get(Fbapi4j.IX_BUG)));
		List<String> allowed = StringUtil.commaDelimitedStringToList(data.get(Fbapi4j.OPERATIONS));
		Set<AllowedOperation> operations = new HashSet<AllowedOperation>();
		for (String op : allowed) {
			operations.add(AllowedOperation.valueOf(op.toUpperCase()));
		}
		c.setAllowedOperations(operations);
	}
}
