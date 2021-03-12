package eu.de4a.connector.mock.exampledata;

import eu.de4a.iem.jaxb.common.types.AgentCVType;
import lombok.Getter;

import java.util.Arrays;

public enum DataOwner {
    COC_NL("iso6523-actorid-upis::9991:NL990000106", "(KVK) Chamber of Commerce of Netherlands", "NL", Pilot.T42),
    V_SE("iso6523-actorid-upis::9991:SE000000013", "(BVE) BOLAGSVERKET (Companies Registration Office)", "SE", Pilot.T42),
    ONRC_RO("iso6523-actorid-upis::9991:RO000000006", "(ORNC) - OFICIUL NATIONAL AL REGISTRULUI COMERTULUI", "RO", Pilot.T42);

    @Getter
    final private String id;
    @Getter
    final private String name;
    @Getter
    final private String country;
    @Getter
    final private Pilot pilot;

    private DataOwner(String id, String name, String country, Pilot pilot) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.pilot = pilot;
    }

    public static DataOwner selectDataOwner(AgentCVType dataOwnerType) {
        return Arrays.stream(DataOwner.values())
                .filter(dataOwner -> dataOwnerType.getIdValue().equals(dataOwner.getId()))
                .findFirst()
                .orElseGet(() -> null);
    }

    @Override
    public String toString() {
        return "DataOwner{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
