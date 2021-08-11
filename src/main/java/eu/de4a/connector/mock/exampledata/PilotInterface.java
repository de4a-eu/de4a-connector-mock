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

    public static PilotInterface multiple(final Pilot... pilots) {
        if (Arrays.stream(pilots)
                .map(Pilot::getDataRequestSubjectRestrictions)
                .distinct()
                .count() != 1) {
            throw new ExampleException("multiple pilots must all have the same DataRequestSubjectRestrictions");
        }
        return new PilotInterface() {
            final Pilot[] pilotArray  = pilots;

            @Override
            public IDE4ACanonicalEvidenceType getCanonicalEvidenceType() {
                return IDE4ACanonicalEvidenceType.multiple(Arrays.stream(pilotArray)
                        .map(Pilot::getCanonicalEvidenceType).toArray(IDE4ACanonicalEvidenceType[]::new));
            }

            @Override
            public DataRequestSubjectRestrictions getDataRequestSubjectRestrictions() {
                return pilotArray[0].getDataRequestSubjectRestrictions();
            }

            @Override
            public boolean validDataRequestSubject(DataRequestSubjectCVType dataRequestSubjectCVType) {
                return pilotArray[0].validDataRequestSubject(dataRequestSubjectCVType);
            }

            @Override
            public String getEIDASIdentifier(DataRequestSubjectCVType dataRequestSubjectCVType) {
                return pilotArray[0].getEIDASIdentifier(dataRequestSubjectCVType);
            }

            @Override
            public String restrictionDescription() {
                return pilotArray[0].restrictionDescription();
            }
        };
    }
}
