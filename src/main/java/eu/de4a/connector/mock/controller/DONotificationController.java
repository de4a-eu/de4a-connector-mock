package eu.de4a.connector.mock.controller;

import static eu.de4a.connector.mock.Helper.sendRequest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.de4a.connector.mock.Helper;
import eu.de4a.connector.mock.config.DOConfig;
import eu.de4a.connector.mock.preview.NotificationStorage;
import eu.de4a.connector.mock.preview.PreviewMessage;
import eu.de4a.connector.mock.preview.SubscriptionRequestStorage;
import eu.de4a.connector.mock.preview.SubscriptionStorage;
import eu.de4a.connector.mock.utils.MessagesHelper;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.jaxb.common.EventNotificationType;
import eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@Profile("do")
public class DONotificationController {

    @Autowired
    SubscriptionStorage subscriptionStorage;
    
    @Autowired
    SubscriptionRequestStorage subscriptionRequestStorage;
    
    @Autowired
    NotificationStorage notificationStorage;
    
    @Autowired
    DOConfig doConfig;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SimpMessagingTemplate websocketMessaging;

    @GetMapping(value = "${mock.do.preview.endpoint.subscription.base}${mock.do.send.notif.subscrip}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendNotification(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
    	EventNotificationType request = new EventNotificationType();
    	request = Helper.buildNotificationFromSubscription(subscriptionStorage.getRequest(requestId).get());
    	RequestEventSubscriptionType subscriptionRequest = subscriptionRequestStorage.getRequest(requestId).get();
    	
    	request.setEventNotificationItem(Helper.buidNotificationItemList(subscriptionStorage.getRequest(requestId).get().getResponseEventSubscriptionItem(),
    			subscriptionStorage.getRequest(requestId).get(), subscriptionRequest));
        try {
            Boolean success = sendRequest(
                    doConfig.getDTUrlNotification(),
                    DE4ACoreMarshaller.dtEventNotificationMarshaller().getAsInputStream(request),
                    log::error).get();
            if (!success) {
                return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Error sending message");
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.debug("request interrupted: {}", ex.getMessage());
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

        return ResponseEntity.ok().body(DE4ACoreMarshaller.dtEventNotificationMarshaller().getAsString(request));
    }
    
    @GetMapping(value = "${mock.do.preview.endpoint.subscription.base}${mock.do.build.notif.subscrip}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> buildNotification(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
    	EventNotificationType notification = new EventNotificationType();
    	notification = Helper.buildNotificationFromSubscription(subscriptionStorage.getRequest(requestId).get());
    	
    	RequestEventSubscriptionType subscriptionRequest = subscriptionRequestStorage.getRequest(requestId).get();
    	
    	notification.setEventNotificationItem(Helper.buidNotificationItemList(subscriptionStorage.getRequest(requestId).get().getResponseEventSubscriptionItem(), subscriptionStorage.getRequest(requestId).get(), subscriptionRequest));
        
    	
    	if(subscriptionRequest != null 
    			&& subscriptionRequest.getEventSubscripRequestItemAtIndex(0) != null
    			&& subscriptionRequest.getEventSubscripRequestItemAtIndex(0).getDataRequestSubject() != null 
    			&& subscriptionRequest.getEventSubscripRequestItemAtIndex(0).getDataRequestSubject().getDataSubjectCompany() != null) {
        	
    		String eidasIdentifier = subscriptionRequest.getEventSubscripRequestItemAtIndex(0).getDataRequestSubject().getDataSubjectCompany().getLegalPersonIdentifier();
        	
        	notification.getEventNotificationItem().forEach((item) -> {
        		if(item.getEventSubject() != null) {
        			if(item.getEventSubject().getDataSubjectCompany() != null)
        				item.getEventSubject().getDataSubjectCompany().setLegalPersonIdentifier(eidasIdentifier);
        			if(item.getEventSubject().getDataSubjectRepresentative() != null)
        				item.getEventSubject().getDataSubjectRepresentative().setPersonIdentifier(eidasIdentifier);
        			if(item.getEventSubject().getDataSubjectPerson() != null)
        				item.getEventSubject().getDataSubjectPerson().setPersonIdentifier(eidasIdentifier);
        		}
        	});
    	}

    	//store  
    	notificationStorage.addRequestToPreview(notification);
    	
        return ResponseEntity.ok().body(DE4ACoreMarshaller.dtEventNotificationMarshaller().getAsString(notification));
    }

    @GetMapping(value = "${mock.do.create.notification}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createNotification(@PathVariable String dataEvaluator, @PathVariable String dataOwner, 
    		@PathVariable String companyName, @PathVariable String company, @PathVariable String event) throws InterruptedException, TimeoutException, ExecutionException {

    	EventNotificationType notification = 
    			MessagesHelper.createEventNotification(3, dataEvaluator, dataOwner, companyName, company, event);
    	//store  
    	notificationStorage.addRequestToPreview(notification);
    	
        return ResponseEntity.ok().body(DE4ACoreMarshaller.dtEventNotificationMarshaller().getAsString(notification));
    }
    
    @GetMapping(value = "${mock.do.send.notification}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendCreatedNotification(@PathVariable String notificationId) throws InterruptedException, TimeoutException, ExecutionException {
    	
    	EventNotificationType request = notificationStorage.getRequest(notificationId).get();
    	
    	try {
            Boolean success = sendRequest(
                    doConfig.getDTUrlNotification(),
                    DE4ACoreMarshaller.dtEventNotificationMarshaller().getAsInputStream(request),
                    log::error).get();
            if (!success) {
                return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Error sending message");
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.debug("request inteupted: {}", ex.getMessage());
            return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("request interupted");
        }
    	
    	notificationStorage.removePreview(notificationId);
        return ResponseEntity.ok().body(DE4ACoreMarshaller.dtEventNotificationMarshaller().getAsString(request));
    }
    
    @GetMapping(value = "${mock.do.preview.endpoint.subscription.base}${mock.do.send.notification}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendNotificationFromSubscription(@PathVariable String notificationId) throws InterruptedException, TimeoutException, ExecutionException {
    	
    	EventNotificationType request = notificationStorage.getRequest(notificationId).get();
    	
    	try {
            Boolean success = sendRequest(
                    doConfig.getDTUrlNotification(),
                    DE4ACoreMarshaller.dtEventNotificationMarshaller().getAsInputStream(request),
                    log::error).get();
            if (!success) {
                return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Error sending message");
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.debug("request inteupted: {}", ex.getMessage());
            return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("request interupted");
        }
    	
    	notificationStorage.removePreview(notificationId);
        return ResponseEntity.ok().body(DE4ACoreMarshaller.dtEventNotificationMarshaller().getAsString(request));
    }
    
}
