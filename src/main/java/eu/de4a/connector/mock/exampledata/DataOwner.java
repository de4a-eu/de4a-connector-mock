package eu.de4a.connector.mock.exampledata;

import eu.de4a.iem.core.jaxb.common.AgentType;
import lombok.Getter;

import java.util.Arrays;

public enum DataOwner {
    AMA_PT("iso6523-actorid-upis::9999:pt000000026-it2", "(AMA IP) Agencia para a Modernizacao Administrativa IP (Administration Modernization Agency)", "PT", Pilot.T43),
    COC_NL("iso6523-actorid-upis::9999:nl990000106-it2", "(KVK) Chamber of Commerce of Netherlands", "NL", Pilot.T42),
    V_SE("iso6523-actorid-upis::9999:se000000013-it2", "(BVE) BOLAGSVERKET (Companies Registration Office)", "SE", Pilot.T42),
    ONRC_RO("iso6523-actorid-upis::9999:ro000000006-it2", "(ORNC) - OFICIUL NATIONAL AL REGISTRULUI COMERTULUI", "RO", Pilot.T42),
    DMDW_AT("iso6523-actorid-upis::9999:at000000271-it2", "(BMDW) Bundesministerium Fuer Digitalisierung Und Wirtschaftsstandort", "AT", Pilot.T42),
    UL_PT("iso6523-actorid-upis::9999:pt990000101-it2", "Portuguese IST, University of Lisbon", "PT", Pilot.T41),
    JSI_SI("iso6523-actorid-upis::9999:si000000018-it2", "(JSI) Institut Jozef Stefan", "SI", Pilot.T41),
    MIZS_SI("iso6523-actorid-upis::9999:si000000016-it2", "(MIZS) Ministrstvo za Izobrazevanje, Znanost in Sport", "SI", Pilot.T41),
    UJI_ES("iso6523-actorid-upis::9999:esq6250003h-it2", "(UJI) Universitat Jaume I de Castellón", "ES", Pilot.T41),
    
    SGAD_ES("iso6523-actorid-upis::9999:ess2833002e-it2", "(MPTFP-SGAD) Secretaría General de Administración Digital", "ES", PilotInterface.multiple(Pilot.T43, Pilot.T41));

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
