package eu.de4a.connector.mock.exampledata;

import lombok.Getter;

import java.util.Arrays;

public enum EvidenceID {
    COMPANY_REGISTRATION("urn:de4a-eu:CanonicalEvidenceType::CompanyRegistration"),
    HIGHER_EDUCATION_DIPLOMA("urn:de4a-eu:CanonicalEvidenceType::HigherEducationDiploma");

    @Getter
    private final String id;

    private EvidenceID(String id) {
        this.id = id;
    }

    public static EvidenceID selectEvidenceId(String evidenceId) {
        return Arrays.stream(EvidenceID.values())
                .filter(eID -> eID.id.equals(evidenceId))
                .findFirst()
                .orElseGet(() -> null);
    }
}
