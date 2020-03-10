package net.rcarz.jiraclient;

import net.rcarz.jiraclient.util.JsonUtil;

import java.net.URI;
import java.util.Map;

/**
 * Represens a Jira filter.
 */
public class Filter extends Resource {

	public Filter() {
	}

	private String name;
	private String jql;
	private boolean favourite;

	public Filter(RestClient restclient, Map json) {
		super(restclient);

		if (json != null)
			deserialise(json);
	}

	private void deserialise(Map json) {
		Map map = json;

		id = Field.getString(map.get("id"));
		self = Field.getString(map.get("self"));
		name = Field.getString(map.get("name"));
		jql = Field.getString(map.get("jql"));
		favourite = Field.getBoolean(map.get("favourite"));
	}

	public boolean isFavourite() {
		return favourite;
	}

	public String getJql() {
		return jql;
	}

	public String getName() {
		return name;
	}

	public static Filter get(final RestClient restclient, final String id) throws JiraException {
		Map result = null;

		try {
			URI uri = restclient.buildURI(getBaseUri() + "filter/" + id);
			String resultJson = restclient.get(uri);
			if (resultJson!=null) {
				result = JsonUtil.OBJECT_MAPPER.readValue(resultJson, Map.class);
			}
		} catch (Exception ex) {
			throw new JiraException("Failed to retrieve filter with id " + id, ex);
		}

		return new Filter(restclient, result);
	}

	@Override
	public String toString() {
		return "Filter{" +
				"favourite=" + favourite +
				", name='" + name + '\'' +
				", jql='" + jql + '\'' +
				'}';
	}


}
