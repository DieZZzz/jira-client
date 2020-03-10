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

import java.util.Map;

/**
 * Represents an issue link type.
 */
public class LinkType extends Resource {

    public LinkType() {
    }

    private String name = null;
    private String inward = null;
    private String outward = null;

    /**
     * Creates a issue link type from a JSON payload.
     *
     * @param restclient REST client instance
     * @param json JSON payload
     */
    protected LinkType(RestClient restclient, Map json) {
        super(restclient);

        if (json != null)
            deserialise(json);
    }

    private void deserialise(Map json) {
        Map map = json;

        self = Field.getString(map.get("self"));
        id = Field.getString(map.get("id"));
        name = Field.getString(map.get("name"));
        inward = Field.getString(map.get("inward"));
        outward = Field.getString(map.get("outward"));
    }

    /**
     * Retrieves the given issue link type record.
     *
     * @param restclient REST client instance
     * @param id Internal JIRA ID of the issue link type
     *
     * @return a issue link type instance
     *
     * @throws JiraException when the retrieval fails
     */
    public static LinkType get(RestClient restclient, String id)
        throws JiraException {

        Map result = null;

        try {
            String resultJson = restclient.get(getBaseUri() + "issueLinkType/" + id);
            if (resultJson!=null) {
                result = JsonUtil.OBJECT_MAPPER.readValue(resultJson, Map.class);
            }
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve issue link type " + id, ex);
        }

        if (result == null)
            throw new JiraException("JSON payload is malformed");

        return new LinkType(restclient, result);
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return name;
    }

    public String getInward() {
        return inward;
    }

    public String getOutward() {
        return outward;
    }
}

