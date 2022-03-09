package eu.de4a.connector.mock.controller;

import static eu.de4a.connector.mock.Helper.sendRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.de4a.connector.mock.Helper;
import eu.de4a.connector.mock.config.DOConfig;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.connector.mock.preview.PreviewMessage;
import eu.de4a.connector.mock.preview.PreviewStorage;
import eu.de4a.connector.mock.preview.SubscriptionStorage;
import eu.de4a.connector.mock.utils.MessagesHelper;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.jaxb.common.EventNotificationType;
import eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;
import lombok.extern.slf4j.Slf4j;

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
    
    @GetMapping(value = "${mock.do.preview.endpoint.subscription.base}${mock.do.preview.endpoint.subscription.index}")
    public String doSubscription(Model model) {
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
    	ResponseExtractMultiEvidenceType request;
        request = previewStorage.getRequest(requestId).get();
        DataOwner dataOwner = DataOwner.selectDataOwner(request.getDataOwner());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DE4ACoreMarshaller.dtResponseTransferEvidenceMarshaller(
                        dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(request));
    }
    
    @GetMapping(value = "${mock.do.preview.endpoint.subscription.base}${mock.do.preview.subscription.get.endpoint}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSubscription(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
    	ResponseEventSubscriptionType request = subscriptionStorage.getRequest(requestId).get();
        DataOwner dataOwner = DataOwner.selectDataOwner(request.getDataOwner());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DE4ACoreMarshaller.dtResponseEventSubscriptionMarshaller().getAsString(request));
    }

    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.evidence.accept.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> acceptEvidence(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
    	ResponseExtractMultiEvidenceType request;
        request = previewStorage.getRequest(requestId).get();

        String redirectUrl = request.getDataEvaluator().getRedirectURL();
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            log.error("no redirect url recieved");
        }
        try {
            Boolean success = sendRequest(
                    doConfig.getPreviewDTUrl(),
                    DE4ACoreMarshaller.dtResponseTransferEvidenceMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsInputStream(request),
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
    	ResponseExtractMultiEvidenceType request;
        request = previewStorage.getRequest(requestId).get();
        request.setResponseExtractEvidenceItem(null);

        String redirectUrl = request.getDataEvaluator().getRedirectURL();
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            log.error("no redirect url recieved");
        }
        try {
            Boolean success = sendRequest(
                    doConfig.getPreviewDTUrl(),
                    DE4ACoreMarshaller.dtResponseTransferEvidenceMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsInputStream(request),
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
    					
    @GetMapping(value = "${mock.do.preview.endpoint.subscription.base}${mock.do.preview.subscription.requestId.all.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllSubscriptionRequestIds() {
        return ResponseEntity.ok(subscriptionStorage.getAllRequestIds());
    }

    @GetMapping(value = "${mock.do.preview.endpoint.subscription.base}${mock.do.preview.subscription.notify.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendNotification(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
    	EventNotificationType request = new EventNotificationType();
    	request = Helper.buildNotificationFromSubscription(subscriptionStorage.getRequest(requestId).get());
    	request.setEventNotificationItem(Helper.buidNotificationItemList(subscriptionStorage.getRequest(requestId).get().getResponseEventSubscriptionItem()));
        
        String redirectUrl = request.getDataEvaluator().getRedirectURL();
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            log.error("no redirect url recieved");
        }
        try {
            Boolean success = sendRequest(
                    doConfig.getPreviewDTUrl(),
                    DE4ACoreMarshaller.dtEventNotificationMarshaller().getAsInputStream(request),
                    log::error).get();
            if (!success) {
                return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Error sending message");
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.debug("request inteupted: {}", ex.getMessage());
            return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("request interupted");
        }
        subscriptionStorage.removePreview(requestId);

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

    //@GetMapping(value = "${mock.do.preview.endpoint.subscription.base}${mock.do.create.notification}", produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "${mock.do.create.notification}", produces = MediaType.APPLICATION_JSON_VALUE)
    //public ResponseEntity<String> createNotification(@PathVariable String content) throws InterruptedException, TimeoutException, ExecutionException {
    public ResponseEntity<String> createNotification(@PathVariable String dataEvaluator, @PathVariable String dataOwner) throws InterruptedException, TimeoutException, ExecutionException {

    	EventNotificationType notification = MessagesHelper.createEventNotification(2, dataEvaluator, dataOwner);
    	//creates a mock notification
    	//EventNotificationType notification = MessagesHelper.createEventNotification(2);
    	
    	
        return ResponseEntity.ok().body(DE4ACoreMarshaller.dtEventNotificationMarshaller().getAsString(notification));
    }
}
