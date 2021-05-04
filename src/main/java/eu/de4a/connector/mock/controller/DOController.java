package eu.de4a.connector.mock.controller;

import com.helger.commons.error.level.EErrorLevel;
import eu.de4a.connector.mock.Helper;
import eu.de4a.connector.mock.config.DOConfig;
import eu.de4a.connector.mock.exampledata.CanonicalEvidenceExamples;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.connector.mock.exampledata.EvidenceID;
import eu.de4a.connector.mock.preview.PreviewStorage;
import eu.de4a.iem.jaxb.common.types.*;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@RestController
@Slf4j
@RequestMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
@Profile("do")
public class DOController {

    @Autowired
    private DOConfig doConfig;
    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private PreviewStorage previewStorage;

    @PostMapping("${mock.do.endpoint.im}")
    public ResponseEntity<String> DO1ImRequestExtractEvidence(InputStream body) {
        var marshaller = DE4AMarshaller.doImRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> MarshallErrorHandler.getInstance().postError(errorKey, ex));
        RequestExtractEvidenceIMType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }

        DE4AKafkaClient.send(EErrorLevel.INFO, String.format("Receiving RequestExtractEvidence, requestId: %s", req.getRequestId()));

        var res = DE4AResponseDocumentHelper.createResponseExtractEvidence(req);
        DataOwner dataOwner = DataOwner.selectDataOwner(req.getDataOwner());
        if (dataOwner == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            ErrorCodes.DE4A_NOT_FOUND.getCode(),
                            String.format("no known data owners with urn %s", req.getDataOwner().getAgentUrn())
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsString(res));
        }
        if (!dataOwner.getPilot().validDataRequestSubject(req.getDataRequestSubject())) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            ErrorCodes.DE4A_BAD_REQUEST.getCode(),
                            String.format("%s for requests to %s", dataOwner.getPilot().restrictionDescription(), dataOwner.toString())
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        EvidenceID evidenceID = EvidenceID.selectEvidenceId(req.getCanonicalEvidenceTypeId());
        if (evidenceID == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            ErrorCodes.DE4A_NOT_FOUND.getCode(),
                            String.format("no known evidence type id '%s'", req.getCanonicalEvidenceTypeId())
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        String eIDASIdentifier = dataOwner.getPilot().getEIDASIdentifier(req.getDataRequestSubject());
        Element canonicalEvidence = CanonicalEvidenceExamples.getDocumentElement(dataOwner, evidenceID, eIDASIdentifier);
        if (canonicalEvidence == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            ErrorCodes.DE4A_NOT_FOUND.getCode(),
                            String.format("No evidence with eIDASIdentifier '%s' found with evidenceID '%s' for %s", eIDASIdentifier, evidenceID.getId(), dataOwner.toString())));
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        CanonicalEvidenceType ce = new CanonicalEvidenceType();
        ce.setAny(canonicalEvidence);
        res.setCanonicalEvidence(ce);

        DE4AKafkaClient.send(EErrorLevel.INFO, String.format("Responding to RequestExtractEvidence, requestId: %s", req.getRequestId()));

        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
    }

    @PostMapping("${mock.do.endpoint.usi}")
    public ResponseEntity<String> DO1USIRequestExtractEvidence(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.doUsiRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestExtractEvidenceUSIType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }

        ResponseErrorType res = DE4AResponseDocumentHelper.createResponseError(true);
        DataOwner dataOwner = DataOwner.selectDataOwner(req.getDataOwner());
        if (dataOwner == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            ErrorCodes.DE4A_NOT_FOUND.getCode(),
                            String.format("no known data owners with urn %s", req.getDataOwner().getAgentUrn())
                    )
            );
            res.setErrorList(errorListType);
            res.setAck(AckType.KO);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(res));
        }
        if (!dataOwner.getPilot().validDataRequestSubject(req.getDataRequestSubject())) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            ErrorCodes.DE4A_BAD_REQUEST.getCode(),
                            String.format("%s for requests to %s", dataOwner.getPilot().restrictionDescription(), dataOwner.toString())
                    )
            );
            res.setErrorList(errorListType);
            res.setAck(AckType.KO);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(res));
        }
        EvidenceID evidenceID = EvidenceID.selectEvidenceId(req.getCanonicalEvidenceTypeId());
        if (evidenceID == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            ErrorCodes.DE4A_NOT_FOUND.getCode(),
                            String.format("no known evidence type id '%s'", req.getCanonicalEvidenceTypeId())
                    )
            );
            res.setErrorList(errorListType);
            res.setAck(AckType.KO);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(res));
        }
        String eIDASIdentifier = dataOwner.getPilot().getEIDASIdentifier(req.getDataRequestSubject());
        CanonicalEvidenceExamples canonicalEvidence = CanonicalEvidenceExamples.getCanonicalEvidence(dataOwner, evidenceID, eIDASIdentifier);
        if (canonicalEvidence == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            ErrorCodes.DE4A_NOT_FOUND.getCode(),
                            String.format("No evidence with eIDASIdentifier '%s' found with evidenceID '%s' for %s", eIDASIdentifier, evidenceID.getId(), dataOwner.toString())));
            res.setErrorList(errorListType);
            res.setAck(AckType.KO);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(res));
        }
        CanonicalEvidenceType ce = new CanonicalEvidenceType();
        ce.setAny(canonicalEvidence.getDocumentElement());

        RequestTransferEvidenceUSIDTType dtRequest = Helper.buildDtUsiRequest(req, ce, null);

        if (canonicalEvidence.getUsiAutoResponse().useAutoResp()) {
            taskScheduler.schedule(() -> sendDTRequest(doConfig.getPreviewDTUrl(), dtRequest, log::error), Instant.now().plusMillis(canonicalEvidence.getUsiAutoResponse().getWait()));
        } else {
            previewStorage.addRequestToPreview(dtRequest);
        }

        DE4AKafkaClient.send(EErrorLevel.INFO, () ->
                String.format("Receiving USI RequestExtractEvidence, requestId: %s", req.getRequestId()));

        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(res));
    }



    public static CompletableFuture<Boolean> sendDTRequest(String recipient, RequestTransferEvidenceUSIDTType request, Consumer<String> onFailure) {
        DataOwner dataOwner = DataOwner.selectDataOwner(request.getDataOwner());
        HttpResponse dtResp;
        try {
            dtResp = Request.Post(recipient)
                    .bodyStream(DE4AMarshaller.dtUsiRequestMarshaller(dataOwner.getPilot().getCanonicalEvidenceType())
                            .getAsInputStream(request), ContentType.APPLICATION_XML)
                    .execute().returnResponse();
        } catch (IOException ex) {
            onFailure.accept(String.format("Failed to send request to dt: %s", ex.getMessage()));
            return CompletableFuture.completedFuture(false);
        }
        if (dtResp.getStatusLine().getStatusCode() != 200) {
            onFailure.accept(String.format("Request sent to dt got status code %s, request %s body: %s \n return body: %s", dtResp.getStatusLine().getStatusCode(),
                    recipient,
                    DE4AMarshaller
                            .dtUsiRequestMarshaller(dataOwner.getPilot().getCanonicalEvidenceType())
                            .getAsString(request),
                    responseBodyToString(dtResp)));
            return CompletableFuture.completedFuture(false);
        }
        log.debug("Successfully sent dt request with id: {}", request.getRequestId());

        return CompletableFuture.completedFuture(true);
    }


    public static String responseBodyToString(HttpResponse response) {
        StringWriter stringWriter = new StringWriter();
        try {
            IOUtils.copy(response.getEntity().getContent(), stringWriter, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return "";
        }
        return stringWriter.toString();
    }
}
