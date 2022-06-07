package eu.de4a.connector.mock.controller;

import static eu.de4a.connector.mock.Helper.doGenericError;
import static eu.de4a.connector.mock.Helper.doIdentityMatchingError;
import static eu.de4a.connector.mock.Helper.sendRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.commons.error.level.EErrorLevel;

import eu.de4a.connector.mock.Helper;
import eu.de4a.connector.mock.config.DOConfig;
import eu.de4a.connector.mock.exampledata.CanonicalEventSubscriptionExamples;
import eu.de4a.connector.mock.exampledata.CanonicalEvidenceExamples;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.connector.mock.exampledata.EvidenceID;
import eu.de4a.connector.mock.exampledata.SubscriptionID;
import eu.de4a.connector.mock.preview.PreviewMessage;
import eu.de4a.connector.mock.preview.PreviewStorage;
import eu.de4a.connector.mock.preview.SubscriptionStorage;
import eu.de4a.iem.cev.EDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.DE4AResponseDocumentHelper;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.jaxb.common.CanonicalEvidenceType;
import eu.de4a.iem.core.jaxb.common.ErrorType;
import eu.de4a.iem.core.jaxb.common.EventSubscripRequestItemType;
import eu.de4a.iem.core.jaxb.common.RedirectUserType;
import eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceItemType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceUSIItemType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType;
import eu.de4a.iem.core.jaxb.common.ResponseErrorType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionItemType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractEvidenceItemType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaClient;
import lombok.extern.slf4j.Slf4j;

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
    private SubscriptionStorage subscriptionStorage;
    @Autowired
    private SimpMessagingTemplate websocketMessaging;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${mock.baseurl}")
    String baseUrl;

    @PostMapping("${mock.do.endpoint.im}")
    public ResponseEntity<String> DO1ImRequestExtractEvidence(InputStream body) throws InterruptedException, ExecutionException {
    	CanonicalEvidenceExamples canonicalEvidence = null;
        var marshaller = DE4ACoreMarshaller.doRequestExtractMultiEvidenceIMMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> MarshallErrorHandler.getInstance().postError(errorKey, ex));
        
        ResponseErrorType response = new ResponseErrorType();
        RequestExtractMultiEvidenceIMType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);       }

        DE4AKafkaClient.send(EErrorLevel.INFO, String.format("Receiving RequestExtractEvidence, requestId: %s", req.getRequestId()));
        
        ResponseExtractMultiEvidenceType res = new ResponseExtractMultiEvidenceType();
        ResponseExtractEvidenceItemType resElement = new ResponseExtractEvidenceItemType();
        DataOwner dataOwner = DataOwner.selectDataOwner(req.getDataOwner());
        if (dataOwner == null) {
            ErrorType errorType = DE4AResponseDocumentHelper.createError ("99999", "no known data owners with urn "+ req.getDataOwner().getAgentUrn());
            response.addError(errorType);
            response.setAck(false);
            return ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
        }
        for (RequestEvidenceItemType reqElement : req.getRequestEvidenceIMItem()) {
        	if (!dataOwner.getPilot().validDataRequestSubject(reqElement.getDataRequestSubject())) {
        		response.addError(doIdentityMatchingError());
                response.addError(
                        DE4AResponseDocumentHelper.createError(
                                MockedErrorCodes.DE4A_BAD_REQUEST.getCode(),
                                String.format("%s for requests to %s", dataOwner.getPilot().restrictionDescription(), dataOwner.toString())
                        )
                );
                response.setAck(false);
                return ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
        	}
        }
        
        for (RequestEvidenceItemType reqElement : req.getRequestEvidenceIMItem()) {
        	EvidenceID evidenceID = EvidenceID.selectEvidenceId(reqElement.getCanonicalEvidenceTypeId());
        	if (evidenceID == null) {
                response.addError(
                        doGenericError(
                                String.format("no known evidence type id '%s'", reqElement.getCanonicalEvidenceTypeId())
                        )
                );
                response.setAck(false);
                return ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
            }
        }
        
       
        for (RequestEvidenceItemType reqElement : req.getRequestEvidenceIMItem()) {
        	EvidenceID evidenceID = EvidenceID.selectEvidenceId(reqElement.getCanonicalEvidenceTypeId());
        	String eIDASIdentifier = dataOwner.getPilot().getEIDASIdentifier(reqElement.getDataRequestSubject());
            canonicalEvidence = CanonicalEvidenceExamples.getCanonicalEvidence(dataOwner, evidenceID, eIDASIdentifier);
            if (canonicalEvidence == null) {
            	response.addError(doIdentityMatchingError());
            	response.addError(
                        DE4AResponseDocumentHelper.createError(
                                MockedErrorCodes.DE4A_NOT_FOUND.getCode(),
                                String.format("No evidence with eIDASIdentifier '%s' found with evidenceID '%s' for %s", eIDASIdentifier, evidenceID.getId(), dataOwner.toString())));
            	response.setAck(false);
            	return ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
            }
        }
        
        res = Helper.buildResponseRequest(req);
        
        CanonicalEvidenceType ce = new CanonicalEvidenceType();
        //ce.setAny(canonicalEvidence);
        ce.setAny(canonicalEvidence.getDocumentElement());
        
        for (RequestEvidenceItemType reqElement : req.getRequestEvidenceIMItem()) {
        	resElement.setRequestItemId(reqElement.getRequestItemId());
        	resElement.setDataRequestSubject(reqElement.getDataRequestSubject());
        	resElement.setCanonicalEvidenceTypeId(reqElement.getCanonicalEvidenceTypeId());
        	resElement.setCanonicalEvidence(ce);
        	res.addResponseExtractEvidenceItem(resElement);
        }
        
        //ResponseExtractMultiEvidenceType request = previewStorage.getRequest("54aaac61-ba3a-4a19-99cf-a085e6fc5eUSI").get();
       /* var marshall = DE4ACoreMarshaller.dtResponseExtractMultiEvidenceMarshaller(EDE4ACanonicalEvidenceType.T42_LEGAL_ENTITY_V06);
        InputStream is2 = marshall.getAsInputStream(res);
        InputStream is = DE4ACoreMarshaller.dtResponseExtractMultiEvidenceMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsInputStream(res);
        */
        try {
            Boolean success = sendRequest(
                    doConfig.getDTEvidenceUrl(),
                    DE4ACoreMarshaller.dtResponseExtractMultiEvidenceMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsInputStream(res),
                    log::error).get();
            if (!success) {
                return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Error sending message");
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.debug("request inteupted: {}", ex.getMessage());
            return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("request interupted");
        }
        
        DE4AKafkaClient.send(EErrorLevel.INFO, String.format("Responding to RequestExtractEvidence, requestId: %s", req.getRequestId()));
        
        response.setAck(true);
        
        return ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
    }
    

    @PostMapping("${mock.do.endpoint.usi}")
    public ResponseEntity<String> DO1USIRequestExtractEvidence(InputStream body) throws MarshallException {
        var marshaller = DE4ACoreMarshaller.doRequestExtractMultiEvidenceUSIMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestExtractMultiEvidenceUSIType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        
        ResponseErrorType response = new ResponseErrorType();
        

        ResponseExtractMultiEvidenceType res = new ResponseExtractMultiEvidenceType();
        ResponseExtractEvidenceItemType resElement = new ResponseExtractEvidenceItemType();
        DataOwner dataOwner = DataOwner.selectDataOwner(req.getDataOwner());
        if (dataOwner == null) {
        	response.addError(
                    doGenericError(
                            String.format("no known data owners with urn %s", req.getDataOwner().getAgentUrn())
                    )
            );
        	response.setAck(false);
            return ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
        }
        for (RequestEvidenceItemType reqElement : req.getRequestEvidenceUSIItem()) {
        	if (!dataOwner.getPilot().validDataRequestSubject(reqElement.getDataRequestSubject())) {
        		response.addError(doIdentityMatchingError());
        		response.addError(
                        DE4AResponseDocumentHelper.createError(
                                MockedErrorCodes.DE4A_BAD_REQUEST.getCode(),
                                String.format("%s for requests to %s", dataOwner.getPilot().restrictionDescription(), dataOwner.toString())
                        )
                );
        		response.setAck(false);
                return ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
        	}
        }
        
        for (RequestEvidenceItemType reqElement : req.getRequestEvidenceUSIItem()) {
        	EvidenceID evidenceID = EvidenceID.selectEvidenceId(reqElement.getCanonicalEvidenceTypeId());
            if (evidenceID == null) {
            	response.addError(
                        doGenericError(
                                String.format("no known evidence type id '%s'", reqElement.getCanonicalEvidenceTypeId())
                        )
                );
                response.setAck(false);
                return ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
            }
        }
        
        
        CanonicalEvidenceExamples canonicalEvidence = null;
        List <CanonicalEvidenceExamples> lCE = new ArrayList();
        for (RequestEvidenceItemType reqElement : req.getRequestEvidenceUSIItem()) {
        	EvidenceID evidenceID = EvidenceID.selectEvidenceId(reqElement.getCanonicalEvidenceTypeId());
        	String eIDASIdentifier = dataOwner.getPilot().getEIDASIdentifier(reqElement.getDataRequestSubject());
	        
	        canonicalEvidence = CanonicalEvidenceExamples.getCanonicalEvidence(dataOwner, evidenceID, eIDASIdentifier);
	        if (canonicalEvidence == null) {
	        	response.addError(doIdentityMatchingError());
	        	response.addError(
	                    DE4AResponseDocumentHelper.createError(
	                            MockedErrorCodes.DE4A_NOT_FOUND.getCode(),
	                            String.format("No evidence with eIDASIdentifier '%s' found with evidenceID '%s' for %s", eIDASIdentifier, evidenceID.getId(), dataOwner.toString())));
	        	response.setAck(false);
	            return ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
	        } else {
	        	lCE.add(canonicalEvidence);
	        }
        }
        
        //CanonicalEvidenceType ce = new CanonicalEvidenceType();
        //ce.setAny(canonicalEvidence.getDocumentElement());
        //canonicalEvidence = lCE.get(0);
        //ce.setAny(canonicalEvidence.getDocumentElement());
        
        res = Helper.buildResponseRequest(req);
        int i = 0;
        for (RequestEvidenceUSIItemType reqElement : req.getRequestEvidenceUSIItem()) {
        	CanonicalEvidenceType ce = new CanonicalEvidenceType();
        	resElement.setDataRequestSubject(reqElement.getDataRequestSubject());
        	resElement.setCanonicalEvidenceTypeId(reqElement.getCanonicalEvidenceTypeId());
        	ce.setAny(lCE.get(i).getDocumentElement());
    		resElement.setCanonicalEvidence(ce);
        	resElement.setRequestItemId(reqElement.getRequestItemId());
        	res.addResponseExtractEvidenceItem(resElement);
        	resElement = new ResponseExtractEvidenceItemType();
        	i++;
        }
        
        
        if (canonicalEvidence.getUsiAutoResponse().useAutoResp()) {
            taskScheduler.schedule(() ->
                    sendRequest(
                            doConfig.getPreviewDTUrl(),
                            DE4ACoreMarshaller.drRequestExtractMultiEvidenceUSIMarshaller().getAsInputStream(req),
                            log::error),
                    Instant.now().plusMillis(canonicalEvidence.getUsiAutoResponse().getWait()));
        } else {
        	res.getDataEvaluator().setRedirectURL(req.getRequestEvidenceUSIItemAtIndex(0).getDataEvaluatorURL());
        	
        	previewStorage.addRequestToPreview(res);
        	
            String message;
            try {
                message = objectMapper.writeValueAsString(new PreviewMessage(PreviewMessage.Action.ADD, req.getRequestId()));
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
            redirectUserType.setDataOwner(req.getDataOwner());
            redirectUserType.setCanonicalEvidenceTypeId(req.getRequestEvidenceUSIItemAtIndex(0).getCanonicalEvidenceTypeId());
            //redirectUserType.setRedirectUrl(req.getRequestEvidenceUSIItemAtIndex(0).getDataEvaluatorURL());

            redirectUserType.setRedirectUrl(
                    String.format("%s%s%s?requestId=%s",
                        baseUrl,
                        doConfig.getPreviewBaseEndpoint(),
                        doConfig.getIndexEndpoint(),
                        req.getRequestId()));
            
            
            
            log.debug("sending redirect message: {}", redirectUserType.getRedirectUrl());
            
            log.debug (DE4ACoreMarshaller.dtUSIRedirectUserMarshaller().formatted ().getAsString (redirectUserType));
           //works with a running & configured connector DT 
            sendRequest(
                    doConfig.getPreviewDTRedirectUrl(),
                    DE4ACoreMarshaller.dtUSIRedirectUserMarshaller().getAsInputStream(redirectUserType),
                    log::error);
                    
        }

        DE4AKafkaClient.send(EErrorLevel.INFO, () ->
                String.format("Receiving USI RequestExtractEvidence, requestId: %s", req.getRequestId()));

        response.setAck(true);
        return ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
    }
    

    @PostMapping("${mock.do.endpoint.subscription}")
    public ResponseEntity<String> DO1SubscriptionRequestEventSubscription(InputStream body) throws MarshallException {
        var marshaller = DE4ACoreMarshaller.doRequestEventSubscriptionMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestEventSubscriptionType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }

        ResponseEventSubscriptionType res = new ResponseEventSubscriptionType();
        ResponseErrorType response = new ResponseErrorType();
                
        DataOwner dataOwner = DataOwner.selectDataOwner(req.getDataOwner());
        if (dataOwner == null) {
        	response.addError(
                    doGenericError(
                            String.format("no known data owners with urn %s", req.getDataOwner().getAgentUrn())
                    )
            );
        	response.setAck(false);
            ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
        }
        for (EventSubscripRequestItemType reqElement : req.getEventSubscripRequestItem()) {
        	if (!dataOwner.getPilot().validDataRequestSubject(reqElement.getDataRequestSubject())) {
        		response.addError(doIdentityMatchingError());
        		response.addError(
	                    DE4AResponseDocumentHelper.createError(
	                            MockedErrorCodes.DE4A_BAD_REQUEST.getCode(),
	                            String.format("%s for requests to %s", dataOwner.getPilot().restrictionDescription(), dataOwner.toString())
	                    )
	            );
        		response.setAck(false);
                ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
	        }
        }
        CanonicalEventSubscriptionExamples canonicalEventSubscription = null;
        for (EventSubscripRequestItemType reqElement : req.getEventSubscripRequestItem()) {
        	SubscriptionID subscriptionID = SubscriptionID.selectSubscriptionID(reqElement.getCanonicalEventCatalogUri());
            if (subscriptionID == null) {
            	response.addError(
                        doGenericError(
                                String.format("no known subscription type id '%s'", reqElement.getCanonicalEventCatalogUri())
                        )
                );
            	response.setAck(false);
                ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
            }
            
            String eIDASIdentifier = dataOwner.getPilot().getEIDASIdentifier(reqElement.getDataRequestSubject());
            
            canonicalEventSubscription = CanonicalEventSubscriptionExamples.getCanonicalEventSubscription(dataOwner, subscriptionID, eIDASIdentifier);
            if (canonicalEventSubscription == null) {
                
                response.addError(
                        DE4AResponseDocumentHelper.createError(
                                MockedErrorCodes.DE4A_NOT_FOUND.getCode(),
                                String.format("No evidence with eIDASIdentifier '%s' found with evidenceID '%s' for %s", eIDASIdentifier, subscriptionID.getId(), dataOwner.toString())));
                response.setAck(false);
                ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
            }
        }
        
        res = Helper.buildSubscriptionResponse(req);
        List<ResponseEventSubscriptionItemType> resElementList = Helper.buildSubscriptionItem(req.getEventSubscripRequestItem());
        res.setResponseEventSubscriptionItem(resElementList);
        final ResponseEventSubscriptionType EventSubscription = res;
        
        if (canonicalEventSubscription.getUsiAutoResponse().useAutoResp()) {
            taskScheduler.schedule(() ->
                    sendRequest(
                            doConfig.getPreviewDTUrl(),
                            DE4ACoreMarshaller.dtResponseEventSubscriptionMarshaller().getAsInputStream(EventSubscription),
                            log::error),
                    Instant.now().plusMillis(canonicalEventSubscription.getUsiAutoResponse().getWait()));
        } else {
        	subscriptionStorage.addRequestToPreview(res);
            String message;
            try {
                message = objectMapper.writeValueAsString(new PreviewMessage(PreviewMessage.Action.ADD, req.getRequestId()));
            } catch (JsonProcessingException ex) {
                message = "{}";
                log.error("json error");
            }
            String endpoint = String.format("%s%s", doConfig.getPreviewBaseEndpoint(), doConfig.getWebsocketMessagesEndpoint());
            log.debug("sending websocket message {}: {}", endpoint, message);
            websocketMessaging.convertAndSend(endpoint, message);

        }

        final ResponseEventSubscriptionType fResponse = res;
        
        DE4AKafkaClient.send(EErrorLevel.INFO, () ->
                String.format("Receiving USI ResponseEventSubscriptionType, requestId: %s", fResponse.getRequestId()));

       
        response.setAck(true);
        
        return ResponseEntity.status(HttpStatus.OK).body(DE4ACoreMarshaller.defResponseErrorMarshaller().getAsString(response));
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
