package be.looorent.notion.port;

import be.looorent.notion.core.Document;

import java.nio.file.Path;

public interface DocumentWriter {
    Path write(Document document, Path folder) throws DocumentWriteException;
    String getName();
}
