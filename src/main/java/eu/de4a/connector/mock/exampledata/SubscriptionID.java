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
package eu.de4a.connector.mock.exampledata;

import java.util.Arrays;

import eu.de4a.iem.cev.EDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import lombok.Getter;

public enum SubscriptionID {
	MARRIAGE_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::MarriageEvidence:1.0", EDE4ACanonicalEvidenceType.T43_MARRIAGE_EVIDENCE_V17),
    BIRTH_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::BirthEvidence:1.0", EDE4ACanonicalEvidenceType.T43_BIRTH_EVIDENCE_V17),
    DOMICILE_REGISTRATION_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::ResidenceRegistration:1.0", EDE4ACanonicalEvidenceType.T43_DOMREG_EVIDENCE_V17),
    COMPANY_REGISTRATION("urn:de4a-eu:CanonicalEvidenceType::CompanyRegistration:1.0", EDE4ACanonicalEvidenceType.T42_LEGAL_ENTITY_V06),
    HIGHER_EDUCATION_DIPLOMA("urn:de4a-eu:CanonicalEvidenceType::HigherEducationDiploma:1.0", EDE4ACanonicalEvidenceType.T41_HIGHER_EDUCATION_EVIDENCE_2022_06_23),
    BUSINESS_EVENTS("urn:de4a-eu:CanonicalEventCatalogueType::BusinessEvents:1.0", EDE4ACanonicalEvidenceType.T42_LEGAL_ENTITY_V06);

	
    @Getter
    private final String id;
    @Getter
    private final IDE4ACanonicalEvidenceType canonicalEvidenceType;

    SubscriptionID(String id, IDE4ACanonicalEvidenceType canonicalEvidenceType) {
        this.id = id;
        this.canonicalEvidenceType = canonicalEvidenceType;
    }

    public static SubscriptionID selectSubscriptionID(String subscriptionID) {
        return Arrays.stream(SubscriptionID.values())
                .filter(eID -> eID.id.equals(subscriptionID))
                .findFirst()
                .orElseGet(() -> null);
    }
}
