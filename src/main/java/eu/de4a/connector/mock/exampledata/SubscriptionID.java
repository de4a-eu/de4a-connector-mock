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
    HIGHER_EDUCATION_DIPLOMA("urn:de4a-eu:CanonicalEvidenceType::HigherEducationDiploma:1.0", EDE4ACanonicalEvidenceType.T41_HIGHER_EDUCATION_EVIDENCE_2021_04_13);

	
    @Getter
    private final String id;
    @Getter
    private final IDE4ACanonicalEvidenceType canonicalEvidenceType;

    private SubscriptionID(String id, IDE4ACanonicalEvidenceType canonicalEvidenceType) {
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
