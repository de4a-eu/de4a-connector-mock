package eu.de4a.connector.mock.controller;

import lombok.Getter;

public enum MockedErrorCodes {
    DE4A_NOT_FOUND("de4a-404"),
    DE4A_BAD_REQUEST("de4a-400"),
    DE4A_ERROR("de4a-500"),
    PREVIEW_REJECTED_ERROR("REJECTED");
    //todo: use a correct error code

    @Getter
    private String code;
    MockedErrorCodes(String code) {
        this.code = code;
    }
}
