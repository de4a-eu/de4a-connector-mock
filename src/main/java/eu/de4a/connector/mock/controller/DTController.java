package eu.de4a.connector.mock.controller;

import com.helger.commons.error.level.EErrorLevel;
import eu.de4a.connector.mock.Helper;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.iem.jaxb.common.types.RequestForwardEvidenceType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.EDE4ACanonicalEvidenceType;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@RestController
@Slf4j
@RequestMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
@Profile("dt")
public class DTController {

    @Value("${mock.dt.forward.enable:false}")
    private boolean forwardUSI;
    @Value("${mock.dt.forward.de.usi:set-some-url}")
    private String forwardUSIUrl;

    @PostMapping("${mock.dt.endpoint.usi}")
    public ResponseEntity<String> dt1usiresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.dtUsiRequestMarshaller(null);
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
            sendDERequest(forwardUSIUrl, deRequest, log::error);
        }

        var res = DE4AResponseDocumentHelper.createResponseError(true);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.dtUsiResponseMarshaller().getAsString(res));
    }

    public static CompletableFuture<Boolean> sendDERequest(String recipient, RequestForwardEvidenceType request, Consumer<String> onFailure) {
        HttpResponse deResp;
        try {
            deResp = Request.Post(recipient)
                    .bodyStream(DE4AMarshaller.deUsiRequestMarshaller(null)
                            .getAsInputStream(request), ContentType.APPLICATION_XML)
                    .execute().returnResponse();
        } catch (IOException ex) {
            onFailure.accept(String.format("Failed to send request to de: %s", ex.getMessage()));
            return CompletableFuture.completedFuture(false);
        }
        if (deResp.getStatusLine().getStatusCode() < 200 && deResp.getStatusLine().getStatusCode() >= 300) { // not a 2XX status code
            onFailure.accept(String.format("Request sent to de got status code %s, request %s body: %s", deResp.getStatusLine().getStatusCode(),
                    recipient,
                    DE4AMarshaller
                            .deUsiRequestMarshaller(null)
                            .getAsString(request)));
            return CompletableFuture.completedFuture(false);
        }
        log.debug("Successfully sent de request with id: {}", request.getRequestId());

        return CompletableFuture.completedFuture(true);
    }
}
