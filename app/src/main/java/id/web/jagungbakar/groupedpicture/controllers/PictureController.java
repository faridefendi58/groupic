package id.web.jagungbakar.groupedpicture.controllers;

import android.content.ContentValues;

import java.util.List;

import id.web.jagungbakar.groupedpicture.models.Pictures;
import id.web.jagungbakar.groupedpicture.utils.Database;
import id.web.jagungbakar.groupedpicture.utils.DatabaseContents;
import id.web.jagungbakar.groupedpicture.utils.DateTimeStrategy;

public class PictureController {
    private static Database database;
    private static PictureController instance;

    private PictureController() {}

    public static PictureController getInstance() {
        if (instance == null)
            instance = new PictureController();
        return instance;
    }

    /**
     * Sets database for use in this class.
     * @param db database.
     */
    public static void setDatabase(Database db) {
        database = db;
    }

    public Object getItems() {
        List<Object> contents = database.select("SELECT * FROM " + DatabaseContents.TABLE_PICTURES);

        return contents;
    }

    public int addPicture(Pictures picture) {
        ContentValues content = new ContentValues();
        content.put("file_name", picture.getName());
        content.put("file_type", picture.getType());
        content.put("file_size", picture.getSize());
        content.put("file_dir", picture.getDir());
        content.put("group_id", 0);
        content.put("date_added", DateTimeStrategy.getCurrentTime());

        int id = database.insert(DatabaseContents.TABLE_PICTURES.toString(), content);

        return id;
    }
}
