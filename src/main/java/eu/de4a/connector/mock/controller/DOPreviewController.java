package eu.de4a.connector.mock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.de4a.connector.mock.preview.PreviewMessage;
import eu.de4a.connector.mock.config.DOConfig;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.connector.mock.preview.PreviewStorage;
import eu.de4a.connector.mock.preview.SubscriptionStorage;
import eu.de4a.iem.jaxb.common.types.*;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static eu.de4a.connector.mock.Helper.doRejectedPreview;
import static eu.de4a.connector.mock.Helper.sendRequest;

@Slf4j
@Controller
@Profile("do")
public class DOPreviewController {

    @Autowired
    PreviewStorage previewStorage;

    @Autowired
    SubscriptionStorage subscriptionStorage;
    
    @Autowired
    DOConfig doConfig;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SimpMessagingTemplate websocketMessaging;

    @Value("${mock.baseurl}")
    String baseUrl;


    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.endpoint.index}")
    public String doIndex(Model model) {
        model.addAttribute("doConfig", doConfig);
        return "doIndex";
    }
    //mock.do.endpoint.subscription=/do1/subscription/eventSubscription
    @GetMapping(value = "${mock.do.endpoint.subscription}")
    public String doNotification(Model model) {
        model.addAttribute("doConfig", doConfig);
        return "doIndex";
    }
    
    @GetMapping(value = "/notification")
    public String doNotificationIndex(Model model) {
        model.addAttribute("doConfig", doConfig);
        return "doIndex";
    }

    @ExceptionHandler({InterruptedException.class, ExecutionException.class, TimeoutException.class})
    public ResponseEntity<Object> timeoutRequest(Exception ex) {
        log.error("Request for evidence failed: {}", ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.evidence.get.endpoint}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getEvidence(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
        RequestTransferEvidenceUSIDTType request;
        request = previewStorage.getRequest(requestId).get();
        DataOwner dataOwner = DataOwner.selectDataOwner(request.getDataOwner());
        return ResponseEntity
                .status(200)
                .body(DE4AMarshaller.dtUsiRequestMarshaller(
                        dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(request));
    }

    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.evidence.accept.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> acceptEvidence(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
        RequestTransferEvidenceUSIDTType request;
        request = previewStorage.getRequest(requestId).get();

        String redirectUrl = request.getDataEvaluator().getRedirectURL();
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            log.error("no redirect url recieved");
        }
        try {
            Boolean success = sendRequest(
                    doConfig.getPreviewDTUrl(),
                    DE4AMarshaller.dtUsiRequestMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsInputStream(request),
                    log::error).get();
            if (!success) {
                return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Error sending message");
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.debug("request inteupted: {}", ex.getMessage());
            return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("request interupted");
        }
        previewStorage.removePreview(requestId);

        String message;
        try {
            message = objectMapper.writeValueAsString(new PreviewMessage(PreviewMessage.Action.RM, requestId));
        } catch (JsonProcessingException ex) {
            message = "{}";
            log.error("json error");
        }
        String endpoint = String.format("%s%s", doConfig.getPreviewBaseEndpoint(), doConfig.getWebsocketMessagesEndpoint());
        log.debug("sending websocket message {}: {}", endpoint, message);
        websocketMessaging.convertAndSend(endpoint, message);

        return ResponseEntity.ok().body(redirectUrl);
    }

    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.evidence.reject.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> rejectEvidence(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
        RequestTransferEvidenceUSIDTType request;
        request = previewStorage.getRequest(requestId).get();
        request.setCanonicalEvidence(null);
        request.setDomesticEvidenceList(null);
        ErrorListType el = new ErrorListType();
        el.addError(doRejectedPreview());
        request.setErrorList(el);

        String redirectUrl = request.getDataEvaluator().getRedirectURL();
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            log.error("no redirect url recieved");
        }
        try {
            Boolean success = sendRequest(
                    doConfig.getPreviewDTUrl(),
                    DE4AMarshaller.dtUsiRequestMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsInputStream(request),
                    log::error).get();
            if (!success) {
                return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Error sending message");
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.debug("request inteupted: {}", ex.getMessage());
            return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("request interupted");
        }
        previewStorage.removePreview(requestId);

        String message;
        try {
            message = objectMapper.writeValueAsString(new PreviewMessage(PreviewMessage.Action.RM, requestId));
        } catch (JsonProcessingException ex) {
            message = "{}";
            log.error("json error");
        }
        String endpoint = String.format("%s%s", doConfig.getPreviewBaseEndpoint(), doConfig.getWebsocketMessagesEndpoint());
        log.debug("sending websocket message {}: {}", endpoint, message);
        websocketMessaging.convertAndSend(endpoint, message);

        return ResponseEntity.ok().body(redirectUrl);
    }

    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.evidence.requestId.all.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllRequestIds() {
        return ResponseEntity.ok(previewStorage.getAllRequestIds());
    }

}
