package eu.de4a.connector.mock.exampledata;

import eu.de4a.iem.jaxb.common.types.AgentType;
import lombok.Getter;

import java.util.Arrays;

public enum DataOwner {
    COC_NL("iso6523-actorid-upis::9999:NL990000106", "(KVK) Chamber of Commerce of Netherlands", "NL", Pilot.T42),
    V_SE("iso6523-actorid-upis::9999:SE000000013", "(BVE) BOLAGSVERKET (Companies Registration Office)", "SE", Pilot.T42),
    ONRC_RO("iso6523-actorid-upis::9999:RO000000006", "(ORNC) - OFICIUL NATIONAL AL REGISTRULUI COMERTULUI", "RO", Pilot.T42),
    DMDW_AT("iso6523-actorid-upis::9999:AT000000271", "(BMDW) Bundesministerium Fuer Digitalisierung Und Wirtschaftsstandort", "AT", Pilot.T42),
    UL_PT("iso6523-actorid-upis::9999:PT990000101", "Portuguese IST, University of Lisbon", "PT", Pilot.T41),
    JSI_SI("iso6523-actorid-upis::9999:SI000000018", "(JSI) Institut Jozef Stefan", "SI", Pilot.T41),
    MIZS_SI("iso6523-actorid-upis::9999:SI000000016", "(MIZS) Ministrstvo za Izobrazevanje, Znanost in Sport", "SI", Pilot.T41),
    UJI_ES("iso6523-actorid-upis::9999:ESQ6250003H", "(UJI) Universitat Jaume I de Castellón", "ES", Pilot.T41);

    @Getter
    final private String urn;
    @Getter
    final private String name;
    @Getter
    final private String country;
    @Getter
    final private Pilot pilot;

    private DataOwner(String urn, String name, String country, Pilot pilot) {
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
