package eu.de4a.connector.mock.preview;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PreviewMessage {

    private Action action;
    private String payload;

    public enum Action {
        ADD,
        RM;
    }
}
