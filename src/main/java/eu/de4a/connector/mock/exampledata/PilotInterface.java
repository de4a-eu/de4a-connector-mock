package eu.de4a.connector.mock.exampledata;

import eu.de4a.iem.jaxb.common.types.DataRequestSubjectCVType;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;

import java.util.Arrays;

public interface PilotInterface {
    public IDE4ACanonicalEvidenceType getCanonicalEvidenceType();
    public DataRequestSubjectRestrictions getDataRequestSubjectRestrictions();
    public boolean validDataRequestSubject(DataRequestSubjectCVType dataRequestSubjectCVType);
    public String getEIDASIdentifier(DataRequestSubjectCVType dataRequestSubjectCVType);
    public String restrictionDescription();

    public static PilotInterface multiple(final PilotInterface... pilots) {
        if (Arrays.stream(pilots)
                .map(PilotInterface::getDataRequestSubjectRestrictions)
                .distinct()
                .count() != 1) {
            throw new ExampleException("multiple pilots must all have the same DataRequestSubjectRestrictions");
        }
        return new PilotInterface() {

            @Override
            public IDE4ACanonicalEvidenceType getCanonicalEvidenceType() {
                IDE4ACanonicalEvidenceType[] evidenceTypes = new IDE4ACanonicalEvidenceType[pilots.length];
                for (int i = 0; i < pilots.length; i++) {
                    evidenceTypes[i] = pilots[i].getCanonicalEvidenceType();
                }
                return IDE4ACanonicalEvidenceType.multiple(evidenceTypes);
                //return IDE4ACanonicalEvidenceType.multiple(Arrays.stream(pilotArray)
                //        .map(Pilot::getCanonicalEvidenceType).toArray(IDE4ACanonicalEvidenceType[]::new));
            }

            @Override
            public DataRequestSubjectRestrictions getDataRequestSubjectRestrictions() {
                return pilots[0].getDataRequestSubjectRestrictions();
            }

            @Override
            public boolean validDataRequestSubject(DataRequestSubjectCVType dataRequestSubjectCVType) {
                return pilots[0].validDataRequestSubject(dataRequestSubjectCVType);
            }

            @Override
            public String getEIDASIdentifier(DataRequestSubjectCVType dataRequestSubjectCVType) {
                return pilots[0].getEIDASIdentifier(dataRequestSubjectCVType);
            }

            @Override
            public String restrictionDescription() {
                return pilots[0].restrictionDescription();
            }
        };
    }
}
