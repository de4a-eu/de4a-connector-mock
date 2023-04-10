/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
