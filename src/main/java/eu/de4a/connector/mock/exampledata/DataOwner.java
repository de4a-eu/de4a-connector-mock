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

import eu.de4a.iem.core.jaxb.common.AgentType;
import lombok.Getter;

import java.util.Arrays;

public enum DataOwner {

    AMA_PT("iso6523-actorid-upis::9999:pt000000026-mock-it2", "(AMA IP) Agencia para a Modernizacao Administrativa IP (Administration Modernization Agency)", "PT", Pilot.T43),
    COC_NL("iso6523-actorid-upis::9999:nl990000106-mock-it2", "(KVK) Chamber of Commerce of Netherlands", "NL", Pilot.T42),
    RVO_NL("iso6523-actorid-upis::9999:nl000000024-mock-it2", "(RVO) Rijksdienst voor Ondernemend Nederland (Netherlands Enterprise Agency)", "NL", Pilot.T42),
    V_SE("iso6523-actorid-upis::9999:se000000013-mock-it2", "(BVE) BOLAGSVERKET (Companies Registration Office)", "SE", Pilot.T42),
    ONRC_RO("iso6523-actorid-upis::9999:ro000000006-mock-it2", "(ORNC) - OFICIUL NATIONAL AL REGISTRULUI COMERTULUI", "RO", Pilot.T42),
    DMDW_AT("iso6523-actorid-upis::9999:at000000271-mock-it2", "(BMDW) Bundesministerium Fuer Digitalisierung Und Wirtschaftsstandort", "AT", Pilot.T42),
    UL_PT("iso6523-actorid-upis::9999:pt990000101-mock-it2", "(INESC-ID) Institute for Systems and Computer Engineering, Technology and Science", "PT", Pilot.T41),
    JSI_SI("iso6523-actorid-upis::9999:si000000018-mock-it2", "(JSI) Institut Jozef Stefan", "SI", Pilot.T41),
    MIZS_SI("iso6523-actorid-upis::9999:si000000016-mock-it2", "(MIZŠ) Ministrstvo za Izobrazevanje, Znanost in Sport (Ministry of Education, Science and Sport)", "SI", Pilot.T41),
    UJI_ES("iso6523-actorid-upis::9999:esq6250003h-mock-it2", "(UJI) Universitat Jaume I de Castellón", "ES", Pilot.T41),
    
    SGAD_ES("iso6523-actorid-upis::9999:ess2833002e-mock-it2", "(MPTFP-SGAD) Secretaría General de Administración Digital", "ES", PilotInterface.multiple(Pilot.T43, Pilot.T41)),
    MOCK_DO("iso6523-actorid-upis::9999:mock-do-localhost-it2", "Mocked DO (Localhost)", "ES", PilotInterface.multiple(Pilot.T41, Pilot.T43)),
    REJECT("iso6523-actorid-upis::9999:ess2833002e-mock-it2-reject", "(MPTFP-SGAD) Secretaría General de Administración Digital", "ES", PilotInterface.multiple(Pilot.T43, Pilot.T41))
    ;


    @Getter
    final private String urn;
    @Getter
    final private String name;
    @Getter
    final private String country;
    @Getter
    final private PilotInterface pilot;

    private DataOwner(String urn, String name, String country, PilotInterface pilot) {
        this.urn = urn;
        this.name = name;
        this.country = country;
        this.pilot = pilot;
    }

    public static DataOwner selectDataOwner(AgentType dataOwnerType) {
        return Arrays.stream(DataOwner.values())
                .filter(dataOwner -> dataOwnerType.getAgentUrn().equalsIgnoreCase(dataOwner.getUrn()))
                .findFirst()
                .orElseGet(() -> null);
    }

    @Override
    public String toString() {
        return "DataOwner{" +
                "id='" + urn + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
