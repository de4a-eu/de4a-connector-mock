package eu.de4a.connector.mock.exampledata;

import lombok.Getter;

public enum USIAutoResponse {
    OFF(false, 0),
    IMMEDIATE(true, 100), //Some delay is still needed to make sure that the dt usi request comes after the do usi request is finished.
    DELAY_5_SEC(true, 5000),
    DELAY_10_SEC(true, 10000),
    DELAY_30_SEC(true, 30000);

    private final boolean useAutoResp;
    @Getter
    private final long wait;

    USIAutoResponse(boolean useAutoResp, long wait) {
        this.useAutoResp = useAutoResp;
        this.wait = wait;
    }

    public boolean useAutoResp() {
        return useAutoResp;
    }
}
