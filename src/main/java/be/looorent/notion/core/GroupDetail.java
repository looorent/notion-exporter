package be.looorent.notion.core;

public class GroupDetail {
    private final String id;
    private final String name;

    public GroupDetail(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
