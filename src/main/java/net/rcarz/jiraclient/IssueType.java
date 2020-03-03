/**
 * jira-client - a simple JIRA REST client
 * Copyright (c) 2013 Bob Carroll (bob.carroll@alum.rit.edu)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.rcarz.jiraclient;

import net.rcarz.jiraclient.util.JsonUtil;

import java.util.List;
import java.util.Map;

/**
 * Represents an issue type.
 */
public class IssueType extends Resource {

    public IssueType() {
    }

    private String description = null;
    private String iconUrl = null;
    private String name = null;
    private boolean subtask = false;
    private Map fields = null;
    private List statuses = null;

    /**
     * Creates an issue type from a JSON payload.
     *
     * @param restclient REST client instance
     * @param json JSON payload
     */
    protected IssueType(RestClient restclient, Map json) {
        super(restclient);

        if (json != null)
            deserialise(json);
    }

    private void deserialise(Map json) {
        Map map = json;

        self = Field.getString(map.get("self"));
        id = Field.getString(map.get("id"));
        description = Field.getString(map.get("description"));
        iconUrl = Field.getString(map.get("iconUrl"));
        name = Field.getString(map.get("name"));
        subtask = Field.getBoolean(map.get("subtask"));

        if (map.containsKey("fields") && map.get("fields") instanceof Map)
            fields = (Map)map.get("fields");

        if (map.containsKey("statuses") && map.get("statuses") instanceof List)
            statuses = (List) map.get("statuses");
    }

    /**
     * Retrieves the given issue type record.
     *
     * @param restclient REST client instance
     * @param id Internal JIRA ID of the issue type
     *
     * @return an issue type instance
     *
     * @throws JiraException when the retrieval fails
     */
    public static IssueType get(RestClient restclient, String id)
        throws JiraException {

        Map result = null;

        try {
            String resultJson = restclient.get(getBaseUri() + "issuetype/" + id);
            if (resultJson!=null) {
                result = JsonUtil.OBJECT_MAPPER.readValue(resultJson, Map.class);
            }
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve issue type " + id, ex);
        }

        if (result == null)
            throw new JiraException("JSON payload is malformed");

        return new IssueType(restclient, result);
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public boolean isSubtask() {
        return subtask;
    }

    public Map getFields() {
        return fields;
    }

    public List getStatuses() {
        return statuses;
    }
}

