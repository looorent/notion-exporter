package be.looorent.notion.port;

import java.nio.file.Path;

public class NotionDatabase implements NotionDocumentable {
    private final String id;
    private final String title;
    private final String groupPropertyName;
    private final String orderPropertyName;
    private transient final Path folder;

    public NotionDatabase(String id,
                          String title,
                          String groupPropertyName,
                          String orderPropertyName,
                          Path folder) {
        this.id = id;
        this.title = title;
        this.groupPropertyName = groupPropertyName;
        this.orderPropertyName = orderPropertyName;
        this.folder = folder;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Path getOutputFolder() {
        return folder;
    }

    public String getGroupPropertyName() {
        return groupPropertyName;
    }

    public String getOrderPropertyName() {
        return orderPropertyName;
    }

    public Path getFolder() {
        return folder;
    }

    public boolean useCustomTitle() {
        return title == null || title.isBlank();
    }

    public boolean useNaturalOrder() {
        return orderPropertyName == null || orderPropertyName.isBlank();
    }

    public boolean useGroups() {
        return groupPropertyName != null && !groupPropertyName.isBlank();
    }

    @Override
    public String toString() {
        var text = "id='" + id + "'";
        if (useCustomTitle()) {
            text += ", Use Notion's title";
        } else {
            text += ", Use title '"+title+",";
        }

        if (useNaturalOrder()) {
            text += ", Use natural order";
        } else {
            text += ", Use order using attribute '"+orderPropertyName+"'";
        }

        if (useGroups()) {
            text += ", Use group using attribute '"+ groupPropertyName +"'";
        } else {
            text += ", Does not use groups";
        }

        return text;
    }
}
