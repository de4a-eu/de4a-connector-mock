package eu.de4a.connector.mock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@ControllerAdvice
public class DE4Advice {

    private String getStackTrace(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    @ExceptionHandler(MarshallException.class)
    private ResponseEntity<String> handleUnmarshallingFailureException(MarshallException marshallException) {
        JAXBException exception;
        try {
            exception = MarshallErrorHandler.getInstance().getError(marshallException.getErrorUUID()).get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            log.error("{}\n{}", ex.getMessage(), getStackTrace(ex));
            return ResponseEntity.badRequest().body("Failed to marshall");
        }
        return ResponseEntity.badRequest().body(String.format("%s\n%s", exception.getCause().getLocalizedMessage(), getStackTrace(exception)));
    }

}
