package net.rcarz.jiraclient;

import net.rcarz.jiraclient.util.JsonUtil;

import java.util.Map;

/**
 * Represent statusCategory of Status.
 */
public class StatusCategory extends Resource {

    public StatusCategory() {
    }

    private String key;

    private String colorName;

    private String name;

    /**
     * Creates a statusCategory from a JSON payload.
     *
     * @param restclient REST client instance
     * @param json JSON payload
     */
    protected StatusCategory(RestClient restclient, Map json) {
        super(restclient);

        if (json != null) {
            deserialise(json);
        }
    }

    private void deserialise(Map json) {

        self = Field.getString(json.get("self"));
        id = Field.getString(json.get("id"));
        key = Field.getString(json.get("key"));
        colorName = Field.getString(json.get("colorName"));
        name = Field.getString(json.get("name"));
    }

    /**
     * Retrieves the given statusCategory record.
     *
     * @param restclient REST client instance
     * @param id Internal JIRA ID of the status
     *
     * @return a statusCategory instance
     *
     * @throws JiraException when the retrieval fails
     */
    public static StatusCategory get(RestClient restclient, String id)
            throws JiraException {

        Map result = null;

        try {
            String resultJson = restclient.get(getBaseUri() + "statuscategory/" + id);
            if (resultJson!=null) {
                result = JsonUtil.OBJECT_MAPPER.readValue(resultJson, Map.class);
            }
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve statusCategory " + id, ex);
        }

        if (result == null)
            throw new JiraException("JSON payload is malformed");

        return new StatusCategory(restclient, result);
    }

    public String getKey() {
        return key;
    }

    public String getColorName() {
        return colorName;
    }

    public String getName() {
        return name;
    }
}
