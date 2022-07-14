package be.looorent.notion.adapter.json;

import be.looorent.notion.port.DocumentWriteException;

class JsonWriteException extends DocumentWriteException {
    protected JsonWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
