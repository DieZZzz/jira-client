package net.rcarz.jiraclient;

import net.sf.json.JSONObject;

import java.util.Map;

/**
 * Represents jira field schema data.
 */
public class JiraFieldSchema {

    private String type = null;
    private String items = null;
    private String system = null;
    private String custom = null;
    private Integer customId = null;

    /**
     * Creates a jira field schema structure from a JSON payload.
     *
     * @param json JSON payload
     */
    protected JiraFieldSchema(JSONObject json) {
        Map<?, ?> map = json;

        type = Field.getString(map.get("type"));
        items = Field.getString(map.get("items"));
        system = Field.getString(map.get("system"));
        custom = Field.getString(map.get("custom"));
        customId = Field.getInteger(map.get("customId"));
    }

    public JiraFieldSchema() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public Integer getCustomId() {
        return customId;
    }

    public void setCustomId(Integer customId) {
        this.customId = customId;
    }
}
