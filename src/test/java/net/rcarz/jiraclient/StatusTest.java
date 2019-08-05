package net.rcarz.jiraclient;

import net.sf.json.JSONObject;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

public class StatusTest {

    private String statusID = "10004";
    private String open = "Open";
    private String statusCategoryID = "10005";
    private String statusCategoryName = "TestName";
    private String statusCategoryKey = "TestKey";
    private String statusCategoryColorName = "colorNameTest";
    private String description = "Issue is currently in progress.";
    private String iconURL = "https://site/images/icons/statuses/open.png";

    @Test
    public void testJSONDeserializer() throws IOException, URISyntaxException {
        Status status = new Status(new RestClient(null, new URI("/123/asd")), getTestJSON());
        assertEquals(status.getDescription(), description);
        assertEquals(status.getIconUrl(), iconURL);
        assertEquals(status.getName(), open);
        assertEquals(status.getId(), statusID);
    }

    @Test
    public void testGetStatus() throws Exception {
        final RestClient restClient = PowerMockito.mock(RestClient.class);
        when(restClient.get(anyString())).thenReturn(getTestJSON());
        Status status = Status.get(restClient,"someID");
        assertEquals(status.getDescription(), description);
        assertEquals(status.getIconUrl(), iconURL);
        assertEquals(status.getName(), open);
        assertEquals(status.getId(), statusID);
        assertNotNull(status.getStatusCategory());
        assertEquals(status.getStatusCategory().getName(), statusCategoryName);
        assertEquals(status.getStatusCategory().getKey(), statusCategoryKey);
        assertEquals(status.getStatusCategory().getId(), statusCategoryID);
        assertEquals(status.getStatusCategory().getColorName(), statusCategoryColorName);
    }

    @Test(expected = JiraException.class)
    public void testJiraExceptionFromRestException() throws Exception {
        final RestClient mockRestClient = PowerMockito.mock(RestClient.class);
        when(mockRestClient.get(anyString())).thenThrow(RestException.class);
        Status.get(mockRestClient, "issueNumber");
    }

    @Test(expected = JiraException.class)
    public void testJiraExceptionFromNonJSON() throws Exception {
        final RestClient mockRestClient = PowerMockito.mock(RestClient.class);
        Status.get(mockRestClient,"issueNumber");
    }

    private JSONObject getTestJSON() {
        JSONObject json = new JSONObject();
        json.element("description", description);
        json.element("name", open);
        json.element("iconUrl", iconURL);
        json.element("id", statusID);
        json.element("statusCategory", getTestStatusCategoryJSON());

        return json;
    }

    private JSONObject getTestStatusCategoryJSON() {
        JSONObject json = new JSONObject();
        json.element("name", statusCategoryName);
        json.element("id", statusCategoryID);
        json.element("key", statusCategoryKey);
        json.element("colorName", statusCategoryColorName);

        return json;
    }

    @Test
    public void testStatusToString() throws URISyntaxException {
        Status status = new Status(new RestClient(null, new URI("/123/asd")), getTestJSON());
        assertEquals(open,status.toString());
    }


}
