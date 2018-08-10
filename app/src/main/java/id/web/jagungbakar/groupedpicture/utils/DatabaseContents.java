package id.web.jagungbakar.groupedpicture.utils;

public enum DatabaseContents {

    DATABASE("gicture.db"),
    TABLE_PICTURES("tbl_pictures"),
    TABLE_GROUPS("tbl_groups");

    private String name;

    /**
     * Constructs DatabaseContents.
     * @param name name of this content for using in database.
     */
    private DatabaseContents(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
