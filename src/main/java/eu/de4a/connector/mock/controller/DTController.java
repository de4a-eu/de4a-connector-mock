package eu.de4a.connector.mock.controller;

import com.helger.commons.error.level.EErrorLevel;
import eu.de4a.connector.mock.Helper;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.iem.jaxb.common.types.RedirectUserType;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.UUID;

import static eu.de4a.connector.mock.Helper.sendRequest;

@RestController
@Slf4j
@RequestMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
@Profile("dt")
public class DTController {

    @Value("${mock.dt.forward.enable:false}")
    private boolean forwardUSI;
    @Value("${mock.dt.forward.de.usi:set-some-url}")
    private String forwardUSIUrl;
    @Value("${mock.dt.forward.de.redirectuser.usi:set-some-url}")
    private String forwardUserRedirectUSIUrl;

    @PostMapping("${mock.dt.endpoint.usi}")
    public ResponseEntity<String> dt1usiresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.dtUsiRequestMarshaller(IDE4ACanonicalEvidenceType.NONE);
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestTransferEvidenceUSIDTType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        DE4AKafkaClient.send(EErrorLevel.INFO,
                String.format("Receiving USI RequestTransferEvidence, requestId: %s", req.getRequestId() ));

        if (forwardUSI) {
            RequestForwardEvidenceType deRequest = Helper.buildDeUriRequest(req);
            DataOwner dataOwner = DataOwner.selectDataOwner(req.getDataOwner());
            if (dataOwner == null) {
                log.error("no such data owner, how the hell did this happen?!");
            }
            sendRequest(forwardUSIUrl, DE4AMarshaller.deUsiRequestMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsInputStream(deRequest), log::error);
        }

        var res = DE4AResponseDocumentHelper.createResponseError(true);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.dtUsiResponseMarshaller().getAsString(res));
    }

    @PostMapping("${mock.dt.endpoint.redirectuser.usi}")
    public ResponseEntity<String> dt1UsiUserRedirect(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.deUsiRedirectUserMarshaller();

        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RedirectUserType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        DE4AKafkaClient.send(EErrorLevel.INFO,
                String.format("Recieving USI RedirectUser, requestId: %s", req.getRequestId()));
        log.debug("Recieved RedirectUser, requestId: {}, redirectUrl: {}", req.getRequestId(), req.getRedirectUrl());

        if (forwardUSI) {
            sendRequest(forwardUserRedirectUSIUrl, DE4AMarshaller.deUsiRedirectUserMarshaller().getAsInputStream(req), log::error);
        }
        var res = DE4AResponseDocumentHelper.createResponseError(true);
        return ResponseEntity.ok().build();
    }
}
