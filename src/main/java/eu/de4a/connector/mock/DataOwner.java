package eu.de4a.connector.mock;

import eu.de4a.iem.jaxb.common.types.AgentCVType;
import lombok.Getter;

import java.util.Arrays;

public enum DataOwner {
    COC_NL("iso6523-actorid-upis::9991:SE990000106", "Chamber of Commerce of Netherlands", "NL", PilotUseCase.T42),
    //todo: correct DO ids
    V_SE("asdf", "VERKSAMT.SE", "SE", PilotUseCase.T42),
    ONRC_RO("qwer", "ONRC", "RO", PilotUseCase.T42);

    @Getter
    final private String id;
    @Getter
    final private String name;
    @Getter
    final private String country;
    @Getter
    final private PilotUseCase pilotUseCase;

    private DataOwner(String id, String name, String country, PilotUseCase pilotUseCase) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.pilotUseCase = pilotUseCase;
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
