package id.web.jagungbakar.groupedpicture.models;

import java.util.HashMap;
import java.util.Map;

public class Groups {
    private int id;
    private String title;
    private String description;

    /**
     * Static value for UNDEFINED ID.
     */
    public static final int UNDEFINED_ID = -1;


    public Groups(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Groups(String title, String description) {
        this(UNDEFINED_ID, title, description);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id + "");
        map.put("title", title);
        map.put("description", description);

        return map;

    }
}
