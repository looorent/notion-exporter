package be.looorent.notion.port;

import java.util.Collection;

public class NotionDatabase implements NotionDocumentable {
    private final String id;
    private final String title;
    private final String groupPropertyName;
    private final String orderPropertyName;
    private transient final Collection<OutputFormat> outputs;

    public NotionDatabase(String id,
                          String title,
                          String groupPropertyName,
                          String orderPropertyName,
                          Collection<OutputFormat> outputs) {
        this.id = id;
        this.title = title;
        this.groupPropertyName = groupPropertyName;
        this.orderPropertyName = orderPropertyName;
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

    @Override
    public Collection<OutputFormat> getOutputs() {
        return outputs;
    }

    public String getGroupPropertyName() {
        return groupPropertyName;
    }

    public String getOrderPropertyName() {
        return orderPropertyName;
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

        text += ", Formats: " + outputs.stream().map(x -> x.getFormat().getId()).toList();

        return text;
    }
}
