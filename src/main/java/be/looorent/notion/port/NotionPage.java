package be.looorent.notion.port;

import java.nio.file.Path;

public class NotionPage implements NotionDocumentable {
    private final String id;
    private final String title;
    private final boolean includeNestedPages;
    private transient final Path folder;

    public NotionPage(String id,
                      String title,
                      boolean includeNestedPages,
                      Path folder) {
        this.id = id;
        this.title = title;
        this.includeNestedPages = includeNestedPages;
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

    public boolean isIncludeNestedPages() {
        return includeNestedPages;
    }

    public Path getFolder() {
        return folder;
    }

    public boolean useCustomTitle() {
        return title == null || title.isBlank();
    }

    @Override
    public String toString() {
        var text = "id='" + id + "'";
        if (useCustomTitle()) {
            text += ", Use Notion's title";
        } else {
            text += ", Use title '"+title+",";
        }

        if (includeNestedPages) {
            text += ", Include nested pages";
        } else {
            text += ", Exclude nested pages";
        }
        return text;
    }
}
