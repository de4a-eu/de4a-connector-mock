package eu.de4a.connector.mock.controller;

import eu.de4a.edm.jaxb.common.types.ErrorType;
import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

import java.util.function.Function;

public class EndpointException extends NestedRuntimeException {

    @Getter
    private Function<ErrorType, String> error;

    public EndpointException(String message, Function<ErrorType, String> error) {
        super(message);
        this.error = error;
    }

    public EndpointException(String message, Throwable cause, Function<ErrorType, String> error) {
        super(message, cause);
        this.error = error;
    }
}

