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
