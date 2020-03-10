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

package net.rcarz.jiraclient.greenhopper;

import net.rcarz.jiraclient.Field;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.RestClient;
import net.rcarz.jiraclient.util.JsonUtil;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GreenHopper backlog data.
 */
public class Backlog {

    private RestClient restclient = null;
    private List<SprintIssue> issues = null;
    private List<SprintIssue> backlogIssues = null;
    private int rankCustomFieldId = 0;
    private List<Sprint> sprints = null;
    private List<RapidViewProject> projects = null;
    private List<Marker> markers = null;
    private List<Epic> epics = null;
    private boolean canEditEpics = false;
    private boolean canManageSprints = false;
    private boolean maxIssuesExceeded = false;
    private int queryResultLimit = 0;
    private Map<String, List<RapidViewVersion>> versionsPerProject = null;

    /**
     * Creates the backlog from a JSON payload.
     *
     * @param restclient REST client instance
     * @param json JSON payload
     */
    protected Backlog(RestClient restclient, Map json) {
        this.restclient = restclient;

        if (json != null)
            deserialise(json);
    }

    private void deserialise(Map json) {
        Map map = json;

        issues = GreenHopperField.getResourceArray(
            SprintIssue.class,
            map.get("issues"),
            restclient);
        rankCustomFieldId = Field.getInteger(map.get("rankCustomFieldId"));
        sprints = GreenHopperField.getResourceArray(
            Sprint.class,
            map.get("sprints"),
            restclient);
        projects = GreenHopperField.getResourceArray(
            RapidViewProject.class,
            map.get("projects"),
            restclient);
        markers = GreenHopperField.getResourceArray(
            Marker.class,
            map.get("markers"),
            restclient);
        canManageSprints = Field.getBoolean(map.get("canManageSprints"));
        maxIssuesExceeded = Field.getBoolean(map.get("maxIssuesExceeded"));
        queryResultLimit = Field.getInteger(map.get("queryResultLimit"));

        if (map.containsKey("epicData") && map.get("epicData") instanceof Map) {
            Map epicData = (Map)map.get("epicData");

            epics = GreenHopperField.getResourceArray(Epic.class, epicData.get("epics"), restclient);
            canEditEpics = Field.getBoolean(epicData.get("canEditEpics"));
        }

        if (map.containsKey("versionData") && map.get("versionData") instanceof Map) {
            Map verData = (Map) map.get("versionData");

            if (verData.containsKey("versionsPerProject") &&
                verData.get("versionsPerProject") instanceof Map) {

                Map verMap = (Map)verData.get("versionsPerProject");
                versionsPerProject = new HashMap<>();

                for (Map.Entry<String, Object> kvp : 
                     (Iterable<Map.Entry<String, Object>>)verMap.entrySet()) {

                    if (!(kvp.getValue() instanceof List))
                        continue;

                    List<RapidViewVersion> versions = new ArrayList<RapidViewVersion>();

                    for (Object item : (List)kvp.getValue()) {
                        if (!(item instanceof Map))
                            continue;

                        RapidViewVersion ver = new RapidViewVersion(restclient, (Map)item);
                        versions.add(ver);
                    }

                    versionsPerProject.put(kvp.getKey(), versions);
                }
            }
        }

        //determining which issues are actually in the backlog vs the sprints
        //fill in the issues into the single sprints and the backlog issue list respectively
        for(SprintIssue issue : issues){
            boolean addedToSprint = false;
            for(Sprint sprint : sprints){
                if(sprint.getIssuesIds().contains(issue.getId())){
                    sprint.getIssues().add(issue);
                    addedToSprint = true;
                }
            }
            if(!addedToSprint){
                if(backlogIssues == null){
                    backlogIssues = new ArrayList<SprintIssue>();
                }
                backlogIssues.add(issue);
            }
        }

    }

    /**
     * Retrieves the backlog data for the given rapid view.
     *
     * @param restclient REST client instance
     * @param rv Rapid View instance
     *
     * @return the backlog
     *
     * @throws JiraException when the retrieval fails
     */
    public static Backlog get(RestClient restclient, RapidView rv)
        throws JiraException {

        final int rvId = rv.getId();
        Map result = null;

        try {
            URI reporturi = restclient.buildURI(
                GreenHopperResource.RESOURCE_URI + "xboard/plan/backlog/data",
                new HashMap<String, String>() {{
                    put("rapidViewId", Integer.toString(rvId));
                }});
            String resultJson = restclient.get(reporturi);
            if (resultJson!=null) {
                result = JsonUtil.OBJECT_MAPPER.readValue(resultJson, Map.class);
            }
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve backlog data", ex);
        }

        if (result == null)
            throw new JiraException("JSON payload is malformed");

        return new Backlog(restclient, result);
    }

    public List<SprintIssue> getIssues() {
        return issues;
    }

    public List<SprintIssue> getBacklogIssues() {
        return backlogIssues;
    }

    public int getRankCustomFieldId() {
        return rankCustomFieldId;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public List<RapidViewProject> getProjects() {
        return projects;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public List<Epic> getEpics() {
        return epics;
    }

    public boolean canEditEpics() {
        return canEditEpics;
    }

    public boolean canManageSprints() {
        return canManageSprints;
    }

    public boolean maxIssuesExceeded() {
        return maxIssuesExceeded;
    }

    public int queryResultLimit() {
        return queryResultLimit;
    }

    public Map<String, List<RapidViewVersion>> getVersionsPerProject() {
        return versionsPerProject;
    }
}

