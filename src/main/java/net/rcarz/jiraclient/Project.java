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

import java.net.URI;
import java.util.*;

/**
 * Represents a JIRA project.
 */
public class Project extends Resource {

    public Project() {
    }

    private Map<String, String> avatarUrls = null;
    private String key = null;
    private String name = null;
    private String description = null;
    private User lead = null;
    private String assigneeType = null;
    private List<Component> components = null;
    private List<IssueType> issueTypes = null;
    private List<Version> versions = null;
    private Map<String, String> roles = null;
    private ProjectCategory category = null;
    private String email = null;

    /**
     * Creates a project from a JSON payload.
     *
     * @param restclient REST client instance
     * @param json JSON payload
     */
    protected Project(RestClient restclient, Map json) {
        super(restclient);

        if (json != null)
            deserialise(json);
    }

    private void deserialise(Map json) {
        Map map = json;

        self = Field.getString(map.get("self"));
        id = Field.getString(map.get("id"));
        avatarUrls = Field.getMap(String.class, String.class, map.get("avatarUrls"));
        key = Field.getString(map.get("key"));
        name = Field.getString(map.get("name"));
        description = Field.getString(map.get("description"));
        lead = Field.getResource(User.class, map.get("lead"), restclient);
        assigneeType = Field.getString(map.get("assigneeType"));
        components = Field.getResourceArray(Component.class, map.get("components"), restclient);
        issueTypes = Field.getResourceArray(
            IssueType.class,
            map.containsKey("issueTypes") ? map.get("issueTypes") : map.get("issuetypes"),
            restclient);
        versions = Field.getResourceArray(Version.class, map.get("versions"), restclient);
        roles = Field.getMap(String.class, String.class, map.get("roles"));
        category = Field.getResource(ProjectCategory.class, map.get( "projectCategory" ), restclient);
        email = Field.getString( map.get("email"));
    }

    /**
     * Retrieves the given project record.
     *
     * @param restclient REST client instance
     * @param key Project key
     *
     * @return a project instance
     *
     * @throws JiraException when the retrieval fails
     */
    public static Project get(RestClient restclient, String key)
        throws JiraException {

        Map result = null;

        try {
            String resultJson = restclient.get(getBaseUri() + "project/" + key);
            if (resultJson!=null) {
                result = JsonUtil.OBJECT_MAPPER.readValue(resultJson, Map.class);
            }
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve project " + key, ex);
        }

        if (result == null)
            throw new JiraException("JSON payload is malformed");

        return new Project(restclient, result);
    }

    public static Map<String, List<String>> getIssueTypesWithStatuses(RestClient restclient, String key) throws JiraException {
        try {
            Map<String, List<String>> result = new HashMap<String, List<String>>();

            URI uri = restclient.buildURI(Resource.getBaseUri() + "project/" + key + "/statuses/");
            String resultJson = restclient.get(uri);
            List resultList = null;
            if (resultJson!=null) {
                resultList = JsonUtil.OBJECT_MAPPER.readValue(resultJson, List.class);
            }
            List<IssueType> issueTypes = Field.getResourceArray(IssueType.class, resultList, restclient);

            for (IssueType issueType : issueTypes) {
                List<String> statusList = new ArrayList<String>();
                List<Status> statuses = Field.getResourceArray(Status.class, issueType.getStatuses(), restclient);
                for (Status status : statuses) {
                    statusList.add(status.getName());
                }
                result.put(issueType.getName(), statusList);
            }

            return result;
        } catch (Exception ex) {
            throw new JiraException(ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param restclient REST client instance
     * @param key project key
     * @return all project IssueLink of the specified project
     * @throws JiraException
     */
    public static List<LinkType> getIssueLinkTypes(RestClient restclient, String key) throws JiraException {
        try {
            URI uri = restclient.buildURI(Resource.getBaseUri() + "issueLinkType");
            String resultJson = restclient.get(uri);
            Map result = null;
            if (resultJson!=null) {
                result = JsonUtil.OBJECT_MAPPER.readValue(resultJson, Map.class);
            }
            if (result!=null) {
                return Field.getResourceArray(LinkType.class, result.get("issueLinkTypes"), restclient);
            } else {
                return Collections.emptyList();
            }
        } catch (Exception ex) {
            throw new JiraException(ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves all project records visible to the session user.
     *
     * @param restclient REST client instance
     *
     * @return a list of projects
     *
     * @throws JiraException when the retrieval fails
     */
    public static List<Project> getAll(RestClient restclient) throws JiraException {
        Map result = null;

        try {
            String resultJson = restclient.get(getBaseUri() + "project");
            if (resultJson!=null) {
                result = JsonUtil.OBJECT_MAPPER.readValue(resultJson, Map.class);
            }
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve projects", ex);
        }

        if (result == null)
            throw new JiraException("JSON payload is malformed");

        return Field.getResourceArray(Project.class, result, restclient);
    }

    @Override
    public String toString() {
        return getName();
    }

    public Map<String, String> getAvatarUrls() {
        return avatarUrls;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public User getLead() {
        return lead;
    }

    public String getAssigneeType() {
        return assigneeType;
    }

    public List<Component> getComponents() {
        return components;
    }

    public List<IssueType> getIssueTypes() {
        return issueTypes;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public Map<String, String> getRoles() {
        return roles;
    }

    public ProjectCategory getCategory() {
        return category;
    }

    public String getEmail() {
        return email;
    }
}

