package eu.de4a.connector.mock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.commons.error.level.EErrorLevel;
import eu.de4a.connector.mock.Helper;
import eu.de4a.connector.mock.config.DOConfig;
import eu.de4a.connector.mock.exampledata.CanonicalEvidenceExamples;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.connector.mock.exampledata.EvidenceID;
import eu.de4a.connector.mock.preview.PreviewMessage;
import eu.de4a.connector.mock.preview.PreviewStorage;
import eu.de4a.iem.jaxb.common.types.*;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Element;

import java.beans.SimpleBeanInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static eu.de4a.connector.mock.Helper.sendRequest;

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
    @Autowired
    private SimpMessagingTemplate websocketMessaging;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${mock.baseurl}")
    String baseUrl;

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

        RequestTransferEvidenceUSIDTType dtRequest = Helper.buildDtUsiRequest(req, ce, null, null);

        if (canonicalEvidence.getUsiAutoResponse().useAutoResp()) {
            taskScheduler.schedule(() ->
                    sendRequest(
                            doConfig.getPreviewDTUrl(),
                            DE4AMarshaller.dtUsiRequestMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsInputStream(dtRequest),
                            log::error),
                    Instant.now().plusMillis(canonicalEvidence.getUsiAutoResponse().getWait()));
        } else {
            previewStorage.addRequestToPreview(dtRequest);
            String message;
            try {
                message = objectMapper.writeValueAsString(new PreviewMessage(PreviewMessage.Action.ADD, dtRequest.getRequestId()));
            } catch (JsonProcessingException ex) {
                message = "{}";
                log.error("json error");
            }
            String endpoint = String.format("%s%s", doConfig.getPreviewBaseEndpoint(), doConfig.getWebsocketMessagesEndpoint());
            log.debug("sending websocket message {}: {}", endpoint, message);
            websocketMessaging.convertAndSend(endpoint, message);

            RedirectUserType redirectUserType = new RedirectUserType();
            redirectUserType.setRequestId(req.getRequestId());
            redirectUserType.setSpecificationId(req.getSpecificationId());
            redirectUserType.setTimeStamp(LocalDateTime.now());
            redirectUserType.setDataEvaluator(req.getDataEvaluator());
            redirectUserType.setCanonicalEvidenceTypeId(req.getCanonicalEvidenceTypeId());
            redirectUserType.setRedirectUrl(
                    String.format("%s%s%s?requestId=%s",
                        baseUrl,
                        doConfig.getPreviewBaseEndpoint(),
                        doConfig.getIndexEndpoint(),
                        req.getRequestId()));
            sendRequest(
                    doConfig.getPreviewDTRedirectUrl(),
                    DE4AMarshaller.deUsiRedirectUserMarshaller().getAsInputStream(redirectUserType),
                    log::error);
        }

        DE4AKafkaClient.send(EErrorLevel.INFO, () ->
                String.format("Receiving USI RequestExtractEvidence, requestId: %s", req.getRequestId()));

        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(res));
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
