/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.de4a.connector.mock.exampledata;

import eu.de4a.iem.core.jaxb.common.DataRequestSubjectCVType;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;

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
            throw new ExampleDataException("multiple pilots must all have the same DataRequestSubjectRestrictions");
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
