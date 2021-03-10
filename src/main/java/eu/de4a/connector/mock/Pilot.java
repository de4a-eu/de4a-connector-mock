package eu.de4a.connector.mock;

import eu.de4a.iem.jaxb.common.types.DataRequestSubjectCVType;
import lombok.Getter;

public enum Pilot {
    T42(DataRequestSubjectRestrictions.LEGAL_ENTITY_REQUIRED);

    @Getter
    private DataRequestSubjectRestrictions dataRequestSubjectRestrictions;

    private Pilot(DataRequestSubjectRestrictions dataRequestSubjectRestrictions) {
        this.dataRequestSubjectRestrictions = dataRequestSubjectRestrictions;
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
