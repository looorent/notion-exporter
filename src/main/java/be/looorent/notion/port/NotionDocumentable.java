package be.looorent.notion.port;

import java.util.Collection;

public interface NotionDocumentable {
    String getId();
    String getTitle();
    Collection<OutputFormat> getOutputs();
}
