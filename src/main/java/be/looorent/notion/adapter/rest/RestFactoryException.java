package be.looorent.notion.adapter.rest;

import be.looorent.notion.port.DocumentFactoryException;

class RestFactoryException extends DocumentFactoryException {
    RestFactoryException(String message) {
        super(message);
    }
}
