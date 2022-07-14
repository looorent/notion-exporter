package be.looorent.notion.port;

import java.nio.file.Path;

public interface NotionDocumentable {
    String getId();
    String getTitle();
    Path getOutputFolder();
}
