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
import lombok.Getter;

import java.util.Arrays;

public enum Pilot implements PilotInterface {
    T43(DataRequestSubjectRestrictions.NATURAL_PERSON_REQUIRED, EvidenceID.MARRIAGE_EVIDENCE, EvidenceID.BIRTH_EVIDENCE, EvidenceID.DOMICILE_REGISTRATION_EVIDENCE, EvidenceID.DOMICILE_DEREGISTRATION_EVIDENCE),
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
            if (dataRequestSubjectCVType.getDataSubjectCompany () == null)
                throw new IllegalStateException ("DRS has no DataSubjectCompany");
            return dataRequestSubjectCVType.getDataSubjectCompany().getLegalPersonIdentifier();
        } 
        
        // DataRequestSubjectRestrictions.NATURAL_PERSON_REQUIRED
        if (dataRequestSubjectCVType.getDataSubjectPerson () == null)
          throw new IllegalStateException ("DRS has no DataSubjectPerson");
        return dataRequestSubjectCVType.getDataSubjectPerson().getPersonIdentifier();
    }

    public String restrictionDescription() {
        if (dataRequestSubjectRestrictions == DataRequestSubjectRestrictions.LEGAL_ENTITY_REQUIRED) {
            return "DataRequestSubject must contain a DataSubjectCompany";
        } else { // DataRequestSubjectRestrictions.NATURAL_PERSON_REQUIRED
            return "DataRequestSubject must contain a DataSubjectPerson";
        }
    }

}
