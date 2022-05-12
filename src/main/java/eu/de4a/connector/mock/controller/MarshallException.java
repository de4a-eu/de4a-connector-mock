package eu.de4a.connector.mock.controller;

import java.util.UUID;

import lombok.Getter;

public class MarshallException extends RuntimeException {

    @Getter
    final private UUID errorUUID;

    public MarshallException(UUID errorUUID) {
        super();
        this.errorUUID = errorUUID;
    }

}
