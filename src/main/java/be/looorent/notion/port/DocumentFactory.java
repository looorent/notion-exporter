package be.looorent.notion.port;

import be.looorent.notion.core.Document;

public interface DocumentFactory {
    Document create(NotionDocumentable database, String token) throws DocumentFactoryException;
}
