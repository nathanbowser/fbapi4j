package com.fieldexpert.fbapi4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import com.fieldexpert.fbapi4j.common.Util;
import com.fieldexpert.fbapi4j.dispatch.Dispatch;
import com.fieldexpert.fbapi4j.dispatch.Request;
import com.fieldexpert.fbapi4j.dispatch.Response;

@EntityConfig(element = "area", list = Fbapi4j.LIST_AREAS, single = Fbapi4j.VIEW_AREA, id = Fbapi4j.IX_AREA, name = Fbapi4j.S_AREA)
class AreaHandler extends AbstractHandler<Area> {

	AreaHandler(Dispatch dispatch, Util util, String token) {
		super(dispatch, util, token);
	}

	@Override
	Area build(Map<String, String> data) {
		Integer owner = null;
		if (data.containsKey(Fbapi4j.IX_PERSON_OWNER)) {
			owner = Integer.parseInt(data.get(Fbapi4j.IX_PERSON_OWNER));
		}
		return new Area(Integer.parseInt(data.get(Fbapi4j.IX_AREA)), data.get(Fbapi4j.S_AREA), owner, Integer.parseInt(data.get(Fbapi4j.IX_PROJECT)));
	}

	public void create(Area area) {
		Map<String, Object> params = new HashMap<String, Object>(area.getFields());
		params.put(Fbapi4j.TOKEN, token);
		Response resp = dispatch.invoke(new Request(Fbapi4j.NEW_AREA, params));
		Node node = resp.getDocument().getElementsByTagName("area").item(0);
		Integer id = Integer.parseInt(util.children(node).get(Fbapi4j.IX_AREA));
		area.setId(id);
	}

	private List<Area> buildAreas(Response resp) {
		List<Map<String, String>> list = util.data(resp.getDocument(), "area");
		List<Area> areas = new ArrayList<Area>();
		for (Map<String, String> map : list) {
			areas.add(build(map));
		}
		return areas;
	}

	public List<Area> getByProject(Integer project) {
		Response resp = dispatch.invoke(new Request(Fbapi4j.LIST_AREAS, util.map(Fbapi4j.TOKEN, token, Fbapi4j.IX_PROJECT, project)));
		return buildAreas(resp);
	}

	@Override
	public Area findByName(String name) {
		throw new Fbapi4jException("Not supported, since Area names do not need to be unique.");
	}
}
