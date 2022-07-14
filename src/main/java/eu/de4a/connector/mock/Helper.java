package eu.de4a.connector.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import com.helger.commons.datetime.XMLOffsetDateTime;

import eu.de4a.connector.mock.utils.MessagesHelper;
import eu.de4a.iem.core.DE4AResponseDocumentHelper;
//import eu.de4a.iem.jaxb.common.types.*;
import eu.de4a.iem.core.jaxb.common.ErrorType;
import eu.de4a.iem.core.jaxb.common.EventNotificationItemType;
import eu.de4a.iem.core.jaxb.common.EventNotificationType;
import eu.de4a.iem.core.jaxb.common.EventSubscripRequestItemType;
import eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionItemType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;

public class Helper {
    public static RequestExtractMultiEvidenceType buildDoImRequest(RequestExtractMultiEvidenceType drRequest) {
        return drRequest.clone ();
    }

    public static RequestExtractMultiEvidenceType buildDoUsiRequest(RequestExtractMultiEvidenceType drRequest) {
        return drRequest.clone ();
    }

  /*  public static RequestExtractMultiEvidenceUSIType buildDtUsiRequest(RequestExtractMultiEvidenceType doRequest, CanonicalEvidenceType canonicalEvidence, DomesticsEvidencesType domesticEvidences, ErrorListType errorListType) {
    	RequestExtractMultiEvidenceUSIType req = new RequestExtractMultiEvidenceUSIType();
        req.setRequestId(doRequest.getRequestId());
        req.setSpecificationId(doRequest.getSpecificationId());
        req.setTimeStamp(LocalDateTime.now());
        req.setProcedureId(doRequest.getProcedureId());
        req.setDataEvaluator(doRequest.getDataEvaluator());
        req.setDataOwner(doRequest.getDataOwner());
        req.setDataRequestSubject(doRequest.getDataRequestSubject());
        req.setCanonicalEvidence(canonicalEvidence);
        req.setDomesticEvidenceList(domesticEvidences);
        req.setErrorList(errorListType);
        return req;
    }*/

    //public static RequestForwardEvidenceType buildDeUriRequest(RequestTransferEvidenceUSIDTType dtRequest) {
   /* public static RequestExtractMultiEvidenceUSIType buildDeUriRequest(ResponseExtractMultiEvidenceType dtRequest) {
    	RequestExtractMultiEvidenceUSIType req = new RequestExtractMultiEvidenceUSIType();
        //req.setRequestId(dtRequest.getRequestId());
        //req.setTimeStamp(LocalDateTime.now());
    	req.setCanonicalEvidence(dtRequest.getCanonicalEvidence());
        req.setDomesticEvidenceList(dtRequest.getDomesticEvidenceList());
        req.setErrorList(dtRequest.getErrorList());
        
    	req.setRequestId(dtRequest.getRequestId());
    	req.setSpecificationId(dtRequest.get);
        req.setCanonicalEvidenceTypeId(dtRequest.get);
        return req;
    }*/
    
    public static ResponseExtractMultiEvidenceType buildResponseRequest (RequestExtractMultiEvidenceType request) {
    	ResponseExtractMultiEvidenceType response = new ResponseExtractMultiEvidenceType();
    	response.setRequestId(request.getRequestId());
    	response.setTimeStamp(request.getTimeStamp());
    	response.setDataEvaluator(request.getDataEvaluator());
    	response.setDataOwner(request.getDataOwner());
    	return response;
    }
    
    public static ResponseEventSubscriptionType buildSubscriptionResponse (RequestEventSubscriptionType request) {
    	ResponseEventSubscriptionType response = new ResponseEventSubscriptionType();
    	response.setRequestId(request.getRequestId());
    	response.setTimeStamp(request.getTimeStamp());
    	response.setDataEvaluator(request.getDataEvaluator());
    	response.setDataOwner(request.getDataOwner());
    	return response;
    }
    
    public static List<ResponseEventSubscriptionItemType> buildSubscriptionItem(List<EventSubscripRequestItemType> eventSubscripRequestItem) {
    	List<ResponseEventSubscriptionItemType> itemListResponse = new ArrayList<>();
		for (EventSubscripRequestItemType item : eventSubscripRequestItem) {
			ResponseEventSubscriptionItemType itemResponse = new ResponseEventSubscriptionItemType();
			itemResponse.setRequestItemId(item.getRequestItemId());
			itemResponse.setCanonicalEventCatalogUri(item.getCanonicalEventCatalogUri());
			itemResponse.setSubscriptionPeriod(item.getSubscriptionPeriod());
			itemListResponse.add(itemResponse);
		}
		return itemListResponse;
	}
    
    public static EventNotificationType buildNotificationFromSubscription(
			ResponseEventSubscriptionType responseEventSubscriptionType) {
    	EventNotificationType notification = new EventNotificationType();
    	notification.setNotificationId(responseEventSubscriptionType.getRequestId());
    	notification.setSpecificationId(responseEventSubscriptionType.getRequestId());
    	notification.setTimeStamp(responseEventSubscriptionType.getTimeStamp());
    	notification.setDataEvaluator(responseEventSubscriptionType.getDataEvaluator());
    	notification.setDataOwner(responseEventSubscriptionType.getDataOwner());
		return notification;
	}
    
    public static List<EventNotificationItemType> buidNotificationItemList(
			List<ResponseEventSubscriptionItemType> list, ResponseEventSubscriptionType responseEventSubscriptionType) {
    	List<EventNotificationItemType> itemListNotification = new ArrayList<>();
		for (ResponseEventSubscriptionItemType item : list) {
			EventNotificationItemType notificationItem = new EventNotificationItemType();
			notificationItem.setNotificationItemId(item.getRequestItemId());
			notificationItem.setEventSubject(MessagesHelper._createDRS());
			notificationItem.setEventId(item.getRequestItemId());
			notificationItem.setCanonicalEventCatalogUri(responseEventSubscriptionType.getResponseEventSubscriptionItem().get(0).getCanonicalEventCatalogUri());
			notificationItem.setEventDate(XMLOffsetDateTime.now());
			itemListNotification.add(notificationItem);
		}
		return itemListNotification;
	}

    public static String getStackTrace(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    public static CompletableFuture<Boolean> sendRequest(String recipient, InputStream bodyStream, Consumer<String> onFailure) {
        HttpResponse dtResp;
        try {
            dtResp = Request.Post(recipient)
                    .bodyStream(bodyStream, ContentType.APPLICATION_XML)
                    .execute().returnResponse();
        } catch (IOException ex) {
            onFailure.accept(String.format("Failed to send request to dt: %s", ex.getMessage()));
            return CompletableFuture.completedFuture(false);
        }
        if (dtResp.getStatusLine().getStatusCode() != 200) {
            onFailure.accept(String.format("Request sent to dt (%s) got status code %s",
                    recipient,
                    dtResp.getStatusLine().getStatusCode()));
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.completedFuture(true);
    }

    public static ErrorType doConnectionError(String service, String explanation) {
        return DE4AResponseDocumentHelper.createError(
                "10503",
                String
                        .format("Connection error with %s - %s", service, explanation)
        );
    }

    public static ErrorType doServiceNotFound(String service) {
        return DE4AResponseDocumentHelper.createError(
                "10501",
                String
                        .format("Service requested %s not found", service)
        );
    }

    public static ErrorType doAccessingData(String service, String explanation) {
        return DE4AResponseDocumentHelper.createError(
                "10506",
                String
                        .format("Connection error with %s - %s", service, explanation)
        );
    }

    public static ErrorType doErrorOnResponse(String service, String explanation) {
        return DE4AResponseDocumentHelper.createError(
                "10504",
                String
                        .format("Error on response from %s - %s", service, explanation)
        );
    }

    public static ErrorType doGenericError(String explanation) {
        return DE4AResponseDocumentHelper.createError(
                "10507",
                explanation
        );
    }

    public static ErrorType doErrorExtractingEvidence() {
        return DE4AResponseDocumentHelper.createError(
                "40510",
                "Error extracting evidence"
        );
    }

    public static ErrorType doEvidenceNotAvailable() {
        return DE4AResponseDocumentHelper.createError(
                "40515",
                "Evidence not available yet (delayed)"
        );
    }

    public static ErrorType doIdentityMatchingError() {
        return DE4AResponseDocumentHelper.createError(
                "40511",
                "Error in identity matching"
        );
    }

    public static ErrorType doUnsuccessfulPreview() {
        return DE4AResponseDocumentHelper.createError(
                "40512",
                "Unsuccessful completion of preview"
        );
    }

    public static ErrorType doFailedReestablishUser() {
        return DE4AResponseDocumentHelper.createError(
                "40514",
                "Failed to re-establish user identity"
        );
    }

    public static ErrorType doRejectedPreview() {
        return DE4AResponseDocumentHelper.createError(
                "40513",
                "Preview rejected by user"
        );
    }

    public static ErrorType doEvidenceError() {
        return DE4AResponseDocumentHelper.createError(
                "40516",
                "evidence"
        );
    }

	

}
