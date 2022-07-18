package be.looorent.notion.port;

import java.util.Collection;

public class NotionPage implements NotionDocumentable {
    private final String id;
    private final String title;
    private final boolean includeNestedPages;
    private transient final Collection<OutputFormat> outputs;

    public NotionPage(String id,
                      String title,
                      boolean includeNestedPages,
                      Collection<OutputFormat> outputs) {
        this.id = id;
        this.title = title;
        this.includeNestedPages = includeNestedPages;
        this.outputs = outputs;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public boolean isIncludeNestedPages() {
        return includeNestedPages;
    }

    public boolean useCustomTitle() {
        return title == null || title.isBlank();
    }

    @Override
    public Collection<OutputFormat> getOutputs() {
        return outputs;
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

        text += ", Formats: " + outputs.stream().map(x -> x.getFormat().getId()).toList();

        return text;
    }
}
