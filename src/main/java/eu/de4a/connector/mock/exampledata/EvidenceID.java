package eu.de4a.connector.mock.exampledata;

import java.util.Arrays;

import eu.de4a.iem.cev.EDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import lombok.Getter;

public enum EvidenceID {
    MARRIAGE_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::MarriageRegistration:1.0", EDE4ACanonicalEvidenceType.T43_MARRIAGE_EVIDENCE_V16B),
    BIRTH_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::BirthCertificate:1.0", EDE4ACanonicalEvidenceType.T43_BIRTH_EVIDENCE_V16B),
    DOMICILE_REGISTRATION_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::ResidenceRegistration:1.0", EDE4ACanonicalEvidenceType.T43_DOMREG_EVIDENCE_V16B),
    COMPANY_REGISTRATION("urn:de4a-eu:CanonicalEvidenceType::CompanyRegistration:1.0", EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V06),
    HIGHER_EDUCATION_DIPLOMA("urn:de4a-eu:CanonicalEvidenceType::HigherEducationDiploma:1.0", EDE4ACanonicalEvidenceType.T41_UC1_2021_04_13),

	
	TEST_EVIDENCE("CanonicalEvidence-1626562515", EDE4ACanonicalEvidenceType.T43_MARRIAGE_EVIDENCE_V16B);
    @Getter
    private final String id;
    @Getter
    private final IDE4ACanonicalEvidenceType canonicalEvidenceType;

    private EvidenceID(String id, IDE4ACanonicalEvidenceType canonicalEvidenceType) {
        this.id = id;
        this.canonicalEvidenceType = canonicalEvidenceType;
    }

    public static EvidenceID selectEvidenceId(String evidenceId) {
        return Arrays.stream(EvidenceID.values())
                .filter(eID -> eID.id.equals(evidenceId))
                .findFirst()
                .orElseGet(() -> null);
    }
}
