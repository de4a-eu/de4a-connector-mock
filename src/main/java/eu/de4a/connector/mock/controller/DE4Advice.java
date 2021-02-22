package eu.de4a.connector.mock.controller;

import eu.de4a.edm.jaxb.common.types.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class DE4Advice {


    @ExceptionHandler(EndpointException.class)
    private ResponseEntity<String> handleUnmarshallingFailureException(EndpointException ex, HttpServletRequest webRequest) {
        ErrorType error = new ErrorType();
        error.setCode("validation");
        error.setText(ex.getLocalizedMessage());
        return ResponseEntity.badRequest().body(ex.getError().apply(error));
    }
}
