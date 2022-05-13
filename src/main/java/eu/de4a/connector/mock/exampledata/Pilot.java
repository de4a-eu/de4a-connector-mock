package eu.de4a.connector.mock.exampledata;

import eu.de4a.iem.core.jaxb.common.DataRequestSubjectCVType;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import lombok.Getter;

import java.util.Arrays;

public enum Pilot implements PilotInterface {
    T43(DataRequestSubjectRestrictions.NATURAL_PERSON_REQUIRED, EvidenceID.MARRIAGE_EVIDENCE, EvidenceID.BIRTH_EVIDENCE, EvidenceID.DOMICILE_REGISTRATION_EVIDENCE),
    T42(DataRequestSubjectRestrictions.LEGAL_ENTITY_REQUIRED, EvidenceID.COMPANY_REGISTRATION),
    T41(DataRequestSubjectRestrictions.NATURAL_PERSON_REQUIRED, EvidenceID.HIGHER_EDUCATION_DIPLOMA);

    @Getter
    private final DataRequestSubjectRestrictions dataRequestSubjectRestrictions;
    private final EvidenceID[] evidenceIDS;

    private Pilot(
            DataRequestSubjectRestrictions dataRequestSubjectRestrictions,
            EvidenceID... evidenceIDS) {
        this.dataRequestSubjectRestrictions = dataRequestSubjectRestrictions;
        this.evidenceIDS = evidenceIDS;
    }

    public IDE4ACanonicalEvidenceType getCanonicalEvidenceType() {
        return IDE4ACanonicalEvidenceType.multiple(
                Arrays.stream(evidenceIDS)
                        .map(EvidenceID::getCanonicalEvidenceType)
                        .toArray(IDE4ACanonicalEvidenceType[]::new));
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
