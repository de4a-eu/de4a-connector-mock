package eu.de4a.connector.mock.exampledata;

import eu.de4a.iem.jaxb.common.types.DataRequestSubjectCVType;
import eu.de4a.iem.xml.de4a.EDE4ACanonicalEvidenceType;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import lombok.Getter;

public enum Pilot {
    T42(DataRequestSubjectRestrictions.LEGAL_ENTITY_REQUIRED, EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V04);

    @Getter
    private final DataRequestSubjectRestrictions dataRequestSubjectRestrictions;
    @Getter
    private final IDE4ACanonicalEvidenceType canonicalEvidenceType;

    private Pilot(DataRequestSubjectRestrictions dataRequestSubjectRestrictions, IDE4ACanonicalEvidenceType canonicalEvidenceType) {
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
            return dataRequestSubjectCVType.getDataSubjectCompany().getLegalEntityIdentifier();
        } else { // DataRequestSubjectRestrictions.NATURAL_PERSON_REQUIRED
            return dataRequestSubjectCVType.getDataSubjectPerson().getIdentifier();
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
