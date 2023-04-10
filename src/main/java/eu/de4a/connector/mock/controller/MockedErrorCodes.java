/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
