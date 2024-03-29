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

import static eu.de4a.connector.mock.Helper.sendRequest;

import java.util.ArrayList;
import java.util.List;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.de4a.connector.mock.config.DOConfig;
import eu.de4a.connector.mock.exampledata.CanonicalEvidenceExamples;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.connector.mock.exampledata.EvidenceID;
import eu.de4a.connector.mock.preview.NotificationStorage;
import eu.de4a.connector.mock.preview.PreviewMessage;
import eu.de4a.connector.mock.preview.PreviewStorage;
import eu.de4a.connector.mock.preview.SubscriptionStorage;
import eu.de4a.connector.mock.utils.MessageUtils;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.jaxb.common.CanonicalEvidenceType;
import eu.de4a.iem.core.jaxb.common.ErrorType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractEvidenceItemType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;
import eu.de4a.kafkaclient.model.ELogMessage;
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
    NotificationStorage notificationStorage;
    
    @Autowired
    DOConfig doConfig;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SimpMessagingTemplate websocketMessaging;

    @Value("${mock.baseurl}")
    String baseUrl;
    
    private final String mockUseCase = "[UC#TEST]";


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
    public ResponseEntity<String> getEvidence(@PathVariable String requestId) throws InterruptedException, ExecutionException {
    	ResponseExtractMultiEvidenceType request;
        request = previewStorage.getRequest(requestId).get();
        DataOwner dataOwner = DataOwner.selectDataOwner(request.getDataOwner());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DE4ACoreMarshaller.dtResponseTransferEvidenceMarshaller(
                        dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(request));
    }
    
    @GetMapping(value = "${mock.do.preview.endpoint.subscription.base}${mock.do.preview.subscription.get.endpoint}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSubscription(@PathVariable String requestId) throws InterruptedException, ExecutionException {
    	ResponseEventSubscriptionType request = subscriptionStorage.getRequest(requestId).get();
        DataOwner dataOwner = DataOwner.selectDataOwner(request.getDataOwner());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(DE4ACoreMarshaller.dtResponseEventSubscriptionMarshaller().getAsString(request));
    }

    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.evidence.accept.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> acceptEvidence(@PathVariable String requestId) throws InterruptedException, ExecutionException {
    	ResponseExtractMultiEvidenceType request;
        request = previewStorage.getRequest(requestId).get();

        String redirectUrl = request.getDataEvaluator().getRedirectURL();
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            log.error("no redirect url recieved");
        }
        try {
            Boolean success = sendRequest(
                    doConfig.getDTEvidenceUrl(),
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
    public ResponseEntity<String> rejectEvidence(@PathVariable String requestId) throws InterruptedException, ExecutionException {
    	ResponseExtractMultiEvidenceType request;
        request = previewStorage.getRequest(requestId).get();
       
        var errors = new ArrayList<ErrorType>();
        ErrorType errorType = MessageUtils.GetErrorType(
        		ELogMessage.LOG_DO_ERROR_PREVIEW_REJECTED, 
        		mockUseCase,
        		"");
		
		errors.add(errorType);
		
		for (ResponseExtractEvidenceItemType responseItem :request.getResponseExtractEvidenceItem()) {
			responseItem.setCanonicalEvidence(null);
			responseItem.setError(errors);
        }
		
        String redirectUrl = request.getDataEvaluator().getRedirectURL();
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            log.error("no redirect url recieved");
        }
        try {
            Boolean success = sendRequest(
                    doConfig.getDTEvidenceUrl(),
                    DE4ACoreMarshaller.dtResponseTransferEvidenceMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsInputStream(request),
                    log::error).get();
            if (!success) {
                return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Error sending message");
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.debug("request interrupted: {}", ex.getMessage());
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

}
