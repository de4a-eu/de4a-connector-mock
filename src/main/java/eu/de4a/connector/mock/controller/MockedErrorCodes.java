package eu.de4a.connector.mock.controller;

import lombok.Getter;

public enum MockedErrorCodes {
    DE4A_NOT_FOUND("de4a-404"),
    DE4A_BAD_REQUEST("de4a-400"),
    DE4A_ERROR("de4a-500"),
    PREVIEW_REJECTED_ERROR("REJECTED"),
    //todo: use a correct error code
	
	DE4A_ERROR_EXTRACTING_EVIDENCE("40510"),
	DE4A_ERROR_IDENTITY_MATCHING("40511"),
	DE4A_ERROR_UNSUCCESSFUL_COMPLETION_OF_PREVIEW("40512"),
	DE4A_ERROR_PREVIEW_REJECTED_BY_USER("40513"),
	DE4A_ERROR_FAILED_TO_RESESTABLISH_USER_IDENTITY("40514"),
	DE4A_ERROR_EVIDENCE_NOT_AVAILABLE("40515");

    @Getter
    private String code;
    MockedErrorCodes(String code) {
        this.code = code;
    }
}
