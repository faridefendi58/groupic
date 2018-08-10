package id.web.jagungbakar.groupedpicture.controllers;

import android.content.ContentValues;

import java.util.List;

import id.web.jagungbakar.groupedpicture.models.Groups;
import id.web.jagungbakar.groupedpicture.utils.Database;
import id.web.jagungbakar.groupedpicture.utils.DatabaseContents;
import id.web.jagungbakar.groupedpicture.utils.DateTimeStrategy;

public class GroupController {
    private static Database database;
    private static GroupController instance;

    private GroupController() {}

    public static GroupController getInstance() {
        if (instance == null)
            instance = new GroupController();
        return instance;
    }

    /**
     * Sets database for use in this class.
     * @param db database.
     */
    public static void setDatabase(Database db) {
        database = db;
    }

    public List<Object> getItems() {
        List<Object> contents = database.select("SELECT * FROM " + DatabaseContents.TABLE_GROUPS);

        return contents;
    }

    public int addGroup(Groups group) {
        ContentValues content = new ContentValues();
        content.put("title", group.getTitle());
        content.put("description", group.getDescription());
        content.put("date_added", DateTimeStrategy.getCurrentTime());

        int id = database.insert(DatabaseContents.TABLE_GROUPS.toString(), content);

        return id;
    }

    public boolean removeGroup(int id) {
        return database.delete(DatabaseContents.TABLE_GROUPS.toString(), id);
    }
}
