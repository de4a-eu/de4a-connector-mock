package eu.de4a.connector.mock.controller;

import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.ResponseErrorType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.EDE4ACanonicalEvidenceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
@Profile("de")
public class DEController {

    @PostMapping("${de.endpoint.usi}")
    public ResponseEntity<String> de1usiresp(InputStream body) throws MarshallException {
        //todo: check dataowner and use CanonicalEvidenceType from Pilot enum.
        var marshaller = DE4AMarshaller.deUsiRequestMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V04);
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestForwardEvidenceType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        ResponseErrorType res = DE4AResponseDocumentHelper.createResponseError(true);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.deUsiResponseMarshaller().getAsString(res));
    }

}
