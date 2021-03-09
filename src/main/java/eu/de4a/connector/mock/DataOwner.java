package eu.de4a.connector.mock;

import eu.de4a.iem.jaxb.common.types.AgentCVType;
import lombok.Getter;

import java.util.Arrays;

public enum DataOwner {
    COC_NL("SE990000106", "Chamber of Commerce of Netherlands", PilotUseCase.T42),
    //todo: correct DO ids
    V_SE("asdf", "VERKSAMT.SE", PilotUseCase.T42),
    ONRC_RO("qwer", "ONRC", PilotUseCase.T42);

    @Getter
    final private String id;
    @Getter
    final private String name;
    @Getter
    final private PilotUseCase pilotUseCase;

    private DataOwner(String id, String name, PilotUseCase pilotUseCase) {
        this.id = id;
        this.name = name;
        this.pilotUseCase = pilotUseCase;
    }

    public static DataOwner selectDataOwner(AgentCVType dataOwnerType) {
        return Arrays.stream(DataOwner.values())
                .filter(dataOwner -> dataOwnerType.getIdValue().equals(dataOwner.getId()))
                .findFirst()
                .orElseGet(() -> null);
    }
}
