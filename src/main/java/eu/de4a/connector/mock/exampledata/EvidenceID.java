package eu.de4a.connector.mock.exampledata;

import java.util.Arrays;

import eu.de4a.iem.cev.EDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import lombok.Getter;

public enum EvidenceID {
    MARRIAGE_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::MarriageEvidence:1.0", EDE4ACanonicalEvidenceType.T43_MARRIAGE_EVIDENCE_V17),
    BIRTH_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::BirthEvidence:1.0", EDE4ACanonicalEvidenceType.T43_BIRTH_EVIDENCE_V17),
    DOMICILE_REGISTRATION_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::ResidenceRegistration:1.0", EDE4ACanonicalEvidenceType.T43_DOMREG_EVIDENCE_V17),
    COMPANY_REGISTRATION("urn:de4a-eu:CanonicalEvidenceType::CompanyRegistration:1.0", EDE4ACanonicalEvidenceType.T42_LEGAL_ENTITY_V06),
    HIGHER_EDUCATION_DIPLOMA("urn:de4a-eu:CanonicalEvidenceType::HigherEducationDiploma:1.0", EDE4ACanonicalEvidenceType.T41_HIGHER_EDUCATION_EVIDENCE_2021_04_13),
    HIGHER_EDUCATION_DIPLOMA_IT2("urn:de4a-eu:CanonicalEvidenceType::HigherEducationDiploma:2.0", EDE4ACanonicalEvidenceType.T41_HIGHER_EDUCATION_EVIDENCE_2021_04_13),
    
    SECONDARY_EDUCATION_DIPLOMA("urn:de4a-eu:CanonicalEvidenceType::SecondaryEducationDiploma:1.0", EDE4ACanonicalEvidenceType.T41_SECONDARY_EDUCATION_EVIDENCE_2022_05_12),
    DISABILITY_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::DisabilityEvidence:1.0", EDE4ACanonicalEvidenceType.T41_DISABILITY_EVIDENCE_2022_05_12),
	LARGE_FAMILY_EVIDENCE("urn:de4a-eu:CanonicalEvidenceType::LargeFamilyEvidence:1.0", EDE4ACanonicalEvidenceType.T41_LARGE_FAMILY_EVIDENCE_2022_05_12),
	
	TEST_EVIDENCE("CanonicalEvidence-1626562515", EDE4ACanonicalEvidenceType.T43_MARRIAGE_EVIDENCE_V17);
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
