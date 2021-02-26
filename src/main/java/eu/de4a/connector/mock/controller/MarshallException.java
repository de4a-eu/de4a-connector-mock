package eu.de4a.connector.mock.controller;

import lombok.Getter;

import java.util.UUID;

public class MarshallException extends RuntimeException {

    @Getter
    final private UUID errorUUID;

    public MarshallException(UUID errorUUID) {
        super();
        this.errorUUID = errorUUID;
    }

}
