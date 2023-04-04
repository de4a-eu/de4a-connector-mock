package eu.de4a.connector.mock.utils;



import org.junit.jupiter.api.Test;

import eu.de4a.iem.core.DE4ACoreMarshaller;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.jaxb.common.EventNotificationType;
import eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MessagesHelperTest {

	
	@Test
    void testRequestCreatorMultiEvidenceIM() {
		RequestExtractMultiEvidenceIMType request = MessagesHelper.createRequestExtractMultiEvidenceIM(2);
		
		log.info("request: {}", request.toString());
		var marshaller = DE4ACoreMarshaller.doRequestExtractMultiEvidenceIMMarshaller();
		String req = marshaller.getAsString(request);
	    log.info (req);
	}
	
	@Test
    void testRequestCreatorMultiEvidenceUSI() {
		RequestExtractMultiEvidenceUSIType request = MessagesHelper.createRequestExtractMultiEvidenceUSI(2);
		
		log.info("request: {}", request.toString());
		var marshaller = DE4ACoreMarshaller.doRequestExtractMultiEvidenceUSIMarshaller();
		String req = marshaller.getAsString(request);
	    log.info (req);
	}
	
	@Test
    void testRequestCreatorEventSubscription() {
		RequestEventSubscriptionType request = MessagesHelper.createRequestEventSubscription(2);
		
		log.info("request: {}", request.toString());
		var marshaller = DE4ACoreMarshaller.doRequestEventSubscriptionMarshaller();
		String req = marshaller.getAsString(request);
	    log.info (req);
	}
	
	@Test
    void testRequestCreatorNotification() {
		EventNotificationType request = MessagesHelper.createEventNotification(2);
		
		log.info("request: {}", request.toString());
		var marshaller = DE4ACoreMarshaller.deEventNotificationMarshaller();
		String req = marshaller.getAsString(request);
	    log.info (req);
	}
	
	@Test
	void testResponseCreatorMultiEvidence() {
		ResponseExtractMultiEvidenceType response = MessagesHelper.createResponseExtractMultiEvidence(2);
		log.info("request: {}", response.toString());
		var marshaller = DE4ACoreMarshaller.dtResponseTransferEvidenceMarshaller(IDE4ACanonicalEvidenceType.NONE);
		String req = marshaller.getAsString(response);
	    log.info (req);
	}
}
