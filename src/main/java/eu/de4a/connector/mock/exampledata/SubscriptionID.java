package eu.de4a.connector.mock.exampledata;

import eu.de4a.iem.xml.de4a.EDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import lombok.Getter;

import java.util.Arrays;

public enum SubscriptionID {
    MARRIAGE_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::MarriageEvidence", EDE4ACanonicalEvidenceType.T43_MARRIAGE_EVIDENCE_V16B),
    BIRTH_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::BirthEvidence", EDE4ACanonicalEvidenceType.T43_BIRTH_EVIDENCE_V16B),
    DOMICILE_REGISTRATION_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::DomicileRegistrationEvidence", EDE4ACanonicalEvidenceType.T43_DOMREG_EVIDENCE_V16B),
    COMPANY_REGISTRATION("urn:de4a-eu:CanonicalEvidenceType::CompanyRegistration", EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V06),
    HIGHER_EDUCATION_DIPLOMA("urn:de4a-eu:CanonicalEvidenceType::HigherEducationDiploma", EDE4ACanonicalEvidenceType.T41_UC1_2021_04_13);

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
