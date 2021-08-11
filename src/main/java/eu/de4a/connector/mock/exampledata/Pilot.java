package eu.de4a.connector.mock.exampledata;

import eu.de4a.iem.jaxb.common.types.DataRequestSubjectCVType;
import eu.de4a.iem.xml.de4a.EDE4ACanonicalEvidenceType;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public enum Pilot implements PilotInterface {
    T43(DataRequestSubjectRestrictions.NATURAL_PERSON_REQUIRED, IDE4ACanonicalEvidenceType.multiple(
                    EDE4ACanonicalEvidenceType.T43_BIRTH_EVIDENCE_V16B,
                    EDE4ACanonicalEvidenceType.T43_DOMREG_EVIDENCE_V16B,
                    EDE4ACanonicalEvidenceType.T43_MARRIAGE_EVIDENCE_V16B)),
    T42(DataRequestSubjectRestrictions.LEGAL_ENTITY_REQUIRED, EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V06),
    T41(DataRequestSubjectRestrictions.NATURAL_PERSON_REQUIRED, EDE4ACanonicalEvidenceType.T41_UC1_2021_04_13);

    @Getter
    private final DataRequestSubjectRestrictions dataRequestSubjectRestrictions;
    @Getter
    private final IDE4ACanonicalEvidenceType canonicalEvidenceType;

    private Pilot(
            DataRequestSubjectRestrictions dataRequestSubjectRestrictions,
            IDE4ACanonicalEvidenceType canonicalEvidenceType ) {
        this.dataRequestSubjectRestrictions = dataRequestSubjectRestrictions;
        this.canonicalEvidenceType = canonicalEvidenceType;
    }

    public boolean validDataRequestSubject(DataRequestSubjectCVType dataRequestSubjectCVType) {
        if (dataRequestSubjectRestrictions == DataRequestSubjectRestrictions.LEGAL_ENTITY_REQUIRED) {
            return dataRequestSubjectCVType.getDataSubjectCompany() != null;
        } else {// DataRequestSubjectRestrictions.NATURAL_PERSON_REQUIRED
            return dataRequestSubjectCVType.getDataSubjectPerson() != null;
        }
    }

    public String getEIDASIdentifier(DataRequestSubjectCVType dataRequestSubjectCVType) {
        if (dataRequestSubjectRestrictions == DataRequestSubjectRestrictions.LEGAL_ENTITY_REQUIRED) {
            return dataRequestSubjectCVType.getDataSubjectCompany().getLegalPersonIdentifier();
        } else { // DataRequestSubjectRestrictions.NATURAL_PERSON_REQUIRED
            return dataRequestSubjectCVType.getDataSubjectPerson().getPersonIdentifier();
        }
    }

    public String restrictionDescription() {
        if (dataRequestSubjectRestrictions == DataRequestSubjectRestrictions.LEGAL_ENTITY_REQUIRED) {
            return "DataRequestSubject must contain a DataSubjectCompany";
        } else { // DataRequestSubjectRestrictions.NATURAL_PERSON_REQUIRED
            return "DataRequestSubject must contain a DataSubjectPerson";
        }
    }

}
