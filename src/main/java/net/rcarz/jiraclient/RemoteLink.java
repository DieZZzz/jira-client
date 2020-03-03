package net.rcarz.jiraclient;

import java.util.Map;

public class RemoteLink extends Resource {

    public RemoteLink() {
    }

    private String remoteUrl;
    private String title;

    public RemoteLink(RestClient restclient, Map json) {
        super(restclient);
        if (json != null)
            deserialise(json);
    }

    private void deserialise(Map json) {
        Map map = json;

        self = Field.getString(map.get("self"));
        id = Field.getString(map.get("id"));
        
        Map object = (Map)map.get("object");
        
        remoteUrl = Field.getString(object.get("url"));
        title = Field.getString(object.get("title"));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }
}
