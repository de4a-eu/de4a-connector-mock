package eu.de4a.connector.mock.controller;

import eu.de4a.edm.jaxb.common.types.ErrorType;
import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

import java.util.function.Function;

public class EndpointException extends NestedRuntimeException {

    @Getter
    final private Function<ErrorType, String> errorResponseBuilder;

    public EndpointException(String message, Function<ErrorType, String> errorResponseBuilder) {
        super(message);
        this.errorResponseBuilder = errorResponseBuilder;
    }

    public EndpointException(String message, Throwable cause, Function<ErrorType, String> errorResponseBuilder) {
        super(message, cause);
        this.errorResponseBuilder = errorResponseBuilder;
    }
}

