package eu.de4a.connector.mock.controller;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import eu.de4a.connector.mock.Helper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class DE4Advice {

    @ExceptionHandler(MarshallException.class)
    private ResponseEntity<String> handleUnmarshallingFailureException(MarshallException marshallException) {
        JAXBException exception;
        try {
            exception = MarshallErrorHandler.getInstance().getError(marshallException.getErrorUUID()).get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            log.error("{}\n{}", ex.getMessage(), Helper.getStackTrace(ex));
            return ResponseEntity.badRequest().body("Failed to marshall");
        }
        return ResponseEntity.badRequest().body(String.format("%s\n%s", exception.getCause().getLocalizedMessage(), Helper.getStackTrace(exception)));
    }

}
