package net.rcarz.jiraclient;

import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class IssueHistory extends Resource {

    public IssueHistory() {
    }

    private static final long serialVersionUID = 1L;
    private User user;
    private ArrayList<IssueHistoryItem> changes;
    private Date created;

    /**
     * Creates an issue history record from a JSON payload.
     *
     * @param restclient REST client instance
     * @param json JSON payload
     */
    protected IssueHistory(RestClient restclient, Map json) {
        super(restclient);

        if (json != null) {
            deserialise(restclient,json);
        }
    }

    public IssueHistory(IssueHistory record, ArrayList<IssueHistoryItem> changes) {
        super(record.restclient);
        user = record.user;
        id = record.id;
        self = record.self;
        created = record.created;
        this.changes = changes;
    }

    private void deserialise(RestClient restclient, Map json) {
        Map map = json;
        self = Field.getString(map.get("self"));
        id = Field.getString(map.get("id"));
        user = new User(restclient,(JSONObject)map.get("author"));
        created = Field.getDateTime(map.get("created"));
        List items = (List) map.get("items");
        changes = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            Object p = items.get(i);
            if (p instanceof Map) {
                changes.add(new IssueHistoryItem(restclient, ((Map) p)));
            }
        }
    }

    public User getUser() {
        return user;
    }

    public ArrayList<IssueHistoryItem> getChanges() {
        return changes;
    }

    public Date getCreated() {
        return created;
    }

}
