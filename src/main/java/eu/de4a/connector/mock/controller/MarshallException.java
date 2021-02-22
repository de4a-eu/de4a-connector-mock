package eu.de4a.connector.mock.controller;

import org.springframework.core.NestedRuntimeException;

public class MarshallException extends NestedRuntimeException {

    public MarshallException(String message) {
        super(message);
    }

    public MarshallException(String message, Throwable cause) {
        super(message, cause);
    }
}
