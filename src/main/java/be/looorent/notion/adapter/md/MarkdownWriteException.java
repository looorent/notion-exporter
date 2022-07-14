package be.looorent.notion.adapter.md;

import be.looorent.notion.port.DocumentWriteException;

class MarkdownWriteException extends DocumentWriteException {
    MarkdownWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
