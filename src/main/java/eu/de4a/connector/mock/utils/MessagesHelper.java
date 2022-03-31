package eu.de4a.connector.mock.utils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.math.MathHelper;

import eu.de4a.iem.core.jaxb.common.AgentType;
import eu.de4a.iem.core.jaxb.common.DataRequestSubjectCVType;
import eu.de4a.iem.core.jaxb.common.EventNotificationItemType;
import eu.de4a.iem.core.jaxb.common.EventNotificationType;
import eu.de4a.iem.core.jaxb.common.EventSubscripRequestItemType;
import eu.de4a.iem.core.jaxb.common.ExplicitRequestType;
import eu.de4a.iem.core.jaxb.common.LegalPersonIdentifierType;
import eu.de4a.iem.core.jaxb.common.NaturalPersonIdentifierType;
import eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceItemType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceLUItemType;
import eu.de4a.iem.core.jaxb.common.RequestEvidenceUSIItemType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceIMType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceLUType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceType;
import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType;
import eu.de4a.iem.core.jaxb.common.RequestGroundsType;
import eu.de4a.iem.core.jaxb.common.TimePeriodType;
import eu.de4a.iem.core.jaxb.eidas.np.GenderType;

public class MessagesHelper {

    public static RequestExtractMultiEvidenceIMType createRequestExtractMultiEvidenceIM(int nItems) {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
        final RequestExtractMultiEvidenceIMType ret = new RequestExtractMultiEvidenceIMType();

        fillRequestExtractMultiEvidenceType(ret, aTLR);
        
        RequestEvidenceItemType item = new RequestEvidenceItemType();
        fillRequestEvidenceItemType(item, aTLR);

        ret.addRequestEvidenceIMItem(item);
        
        IntStream.range(1, nItems).forEach(i -> {
            RequestEvidenceItemType newItem = item.clone();
            newItem.setRequestItemId(UUID.randomUUID ().toString ());
            ret.addRequestEvidenceIMItem(newItem);
        });        
        
        return ret;
    }
    
    public static RequestExtractMultiEvidenceUSIType createRequestExtractMultiEvidenceUSI(int nItems) {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
        final RequestExtractMultiEvidenceUSIType ret = new RequestExtractMultiEvidenceUSIType();

        fillRequestExtractMultiEvidenceType(ret, aTLR);
        
        RequestEvidenceUSIItemType item = new RequestEvidenceUSIItemType();
        fillRequestEvidenceItemType(item, aTLR);
        item.setDataEvaluatorURL("http://localhost:8080/");

        ret.addRequestEvidenceUSIItem(item);
        
        IntStream.range(1, nItems).forEach(i -> {
            RequestEvidenceUSIItemType newItem = item.clone();
            newItem.setRequestItemId(UUID.randomUUID ().toString ());
            newItem.setDataEvaluatorURL("http://localhost:8080/");
            ret.addRequestEvidenceUSIItem(newItem);
        });        
        
        return ret;
    }
    
    public static RequestExtractMultiEvidenceLUType createRequestExtractMultiEvidenceLU(int nItems) {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
        final RequestExtractMultiEvidenceLUType ret = new RequestExtractMultiEvidenceLUType();

        fillRequestExtractMultiEvidenceType(ret, aTLR);
        
        RequestEvidenceLUItemType item = new RequestEvidenceLUItemType();
        fillRequestEvidenceItemType(item, aTLR);
        item.setEventNotificationRef(UUID.randomUUID ().toString ());

        ret.addRequestEvidenceLUItem(item);
        
        IntStream.range(1, nItems).forEach(i -> {
            RequestEvidenceLUItemType newItem = item.clone();
            newItem.setRequestItemId(UUID.randomUUID ().toString ());
            newItem.setEventNotificationRef(UUID.randomUUID ().toString ());
            ret.addRequestEvidenceLUItem(newItem);
        });        
        
        return ret;
    }
    
    public static RequestEventSubscriptionType createRequestEventSubscription(int nItems) {
    	final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
        final RequestEventSubscriptionType ret = new RequestEventSubscriptionType();
        
        fillRequestExtractMultiEvidenceType(ret, aTLR);
        
        EventSubscripRequestItemType item = new EventSubscripRequestItemType();
        fillEventSubscripRequestItemType(item, aTLR);

        IntStream.range(1, nItems).forEach(i -> {
        	EventSubscripRequestItemType newItem = item.clone();
            newItem.setRequestItemId(UUID.randomUUID ().toString ());
            ret.addEventSubscripRequestItem(newItem);
        });
        
        return ret;
    }
    
    public static EventNotificationType createEventNotification(int nItems) {
    	final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    	final EventNotificationType notif = new EventNotificationType();
    	
    	fillEventNotificationType(notif, aTLR);
    	
    	EventNotificationItemType item = new EventNotificationItemType();
    	fillEventNotificationItemType(item, aTLR, nItems);
    	
    	IntStream.range(1, nItems).forEach(i -> {
    		EventNotificationItemType newItem = item.clone();
            newItem.setNotificationItemId(UUID.randomUUID ().toString ());
            newItem.setEventId(UUID.randomUUID ().toString ());
            notif.addEventNotificationItem(newItem);
        });
    	return notif;
    }
    
    public static EventNotificationType createEventNotification(int nItems, String dataEvaluator, String dataOwner) {
    	final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    	final EventNotificationType notif = new EventNotificationType();
    	
    	fillEventNotificationType(notif, aTLR);
    	
    	notif.getDataEvaluator().setAgentUrn(dataEvaluator);
    	notif.getDataOwner().setAgentUrn(dataOwner);
    	
    	EventNotificationItemType item = new EventNotificationItemType();
    	fillEventNotificationItemType(item, aTLR, nItems);
    	
    	IntStream.range(1, nItems).forEach(i -> {
    		EventNotificationItemType newItem = item.clone();
            newItem.setNotificationItemId(UUID.randomUUID ().toString ());
            newItem.setEventId(UUID.randomUUID ().toString ());
            notif.addEventNotificationItem(newItem);
        });
    	return notif;
	}
    
    public static EventNotificationType createEventNotification(int nItems, String dataEvaluator, String dataOwner,
			String companyName, String company, String event) {
    	final ThreadLocalRandom aTLR = ThreadLocalRandom.current ();
    	final EventNotificationType notif = new EventNotificationType();
    	
    	fillEventNotificationType(notif, aTLR);
    	
    	notif.getDataEvaluator().setAgentUrn(dataEvaluator);
    	notif.getDataOwner().setAgentUrn(dataOwner);
    	
    	EventNotificationItemType item = new EventNotificationItemType();
    	fillEventNotificationItemType(item, aTLR, nItems, companyName, company, event);
    	
    	IntStream.range(1, nItems).forEach(i -> {
    		EventNotificationItemType newItem = item.clone();
            newItem.setNotificationItemId(UUID.randomUUID ().toString ());
            newItem.setEventId(UUID.randomUUID ().toString ());
            notif.addEventNotificationItem(newItem);
        });
    	return notif;
	}
    
	private static void fillEventNotificationType(EventNotificationType notif, ThreadLocalRandom aTLR) {
		notif.setNotificationId(UUID.randomUUID ().toString ());
		notif.setSpecificationId ("Specification-" + MathHelper.abs (aTLR.nextInt ()));
		notif.setTimeStamp (PDTFactory.getCurrentLocalDateTime ());
		notif.setDataEvaluator (_createAgent ());
        notif.setDataOwner (_createAgent ());
	}

	private static void fillRequestExtractMultiEvidenceType(RequestExtractMultiEvidenceType req,
            ThreadLocalRandom aTLR) {        
        req.setRequestId (UUID.randomUUID ().toString ());
        req.setSpecificationId ("Specification-" + MathHelper.abs (aTLR.nextInt ()));
        req.setTimeStamp (PDTFactory.getCurrentLocalDateTime ());
        req.setProcedureId ("Procedure-" + MathHelper.abs (aTLR.nextInt ()));
        req.setDataEvaluator (_createAgent ());
        req.setDataOwner (_createAgent ());
    }
    
    private static void fillRequestEvidenceItemType(RequestEvidenceItemType item,
            ThreadLocalRandom aTLR) {
        item.setRequestItemId(UUID.randomUUID ().toString ());
        item.setDataRequestSubject (_createDRS ());
        item.setRequestGrounds (_createRequestGrounds ());
        item.setCanonicalEvidenceTypeId ("CanonicalEvidence-" + MathHelper.abs (aTLR.nextInt ()));
    }

    private static void fillEventSubscripRequestItemType(EventSubscripRequestItemType item, 
            ThreadLocalRandom aTLR) {
        item.setRequestItemId(UUID.randomUUID ().toString ());
        item.setDataRequestSubject (_createDRS ());
        item.setCanonicalEventCatalogUri("URI");
        TimePeriodType period = new TimePeriodType();
        period.setStartDate(LocalDateTime.now());
        period.setEndDate(LocalDateTime.now());
        item.setSubscriptionPeriod(period);
    }
    
    private static void fillEventNotificationItemType(EventNotificationItemType item, ThreadLocalRandom aTLR, int nItems) {
		item.setNotificationItemId(UUID.randomUUID ().toString ());
		item.setEventId(UUID.randomUUID ().toString ());
		item.setEventSubject(_createDRS ());
		item.setCanonicalEventCatalogUri("URI");
		item.setEventDate(LocalDateTime.now());
		IntStream.range(1, nItems).forEach(i -> {
			item.addRelatedEventSubject(_createDRS());
        });
	}
    
    private static void fillEventNotificationItemType(EventNotificationItemType item, ThreadLocalRandom aTLR, int nItems, String companyName, String company, String event) {
		item.setNotificationItemId(UUID.randomUUID ().toString ());
		item.setEventId(UUID.randomUUID ().toString ());
		item.setEventSubject(_createDRS (companyName, company, event));
		item.setCanonicalEventCatalogUri("URI");
		item.setEventDate(LocalDateTime.now());
		IntStream.range(1, nItems).forEach(i -> {
			item.addRelatedEventSubject(_createDRS());
        });
	}
    
	@Nonnull
    private static AgentType _createAgent() {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current();
        final AgentType ret = new AgentType();
        ret.setAgentUrn("urn-" + MathHelper.abs(aTLR.nextInt()));
        ret.setAgentName("Buck Hill " + MathHelper.abs(aTLR.nextInt()));
        //ret.setRedirectURL("http://localhost:8077/de4a-connector/");
        return ret;
    }

    @Nonnull
    static <T> T random(@Nonnull final T[] a) {
        return a[ThreadLocalRandom.current().nextInt(a.length)];
    }

    @Nonnull
    private static NaturalPersonIdentifierType _createNP() {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current();
        final NaturalPersonIdentifierType ret = new NaturalPersonIdentifierType();
        ret.setPersonIdentifier("ID-" + MathHelper.abs(aTLR.nextInt()));
        ret.setFirstName("FirstName-" + MathHelper.abs(aTLR.nextInt()));
        ret.setFamilyName("FamilyName-" + MathHelper.abs(aTLR.nextInt()));
        ret.setDateOfBirth(PDTFactory.getCurrentLocalDate().minusYears(18 + aTLR.nextInt(50)));
        ret.setGender(random(GenderType.values()));
        // Ignore the optional stuff
        return ret;
    }

    @Nonnull
    private static LegalPersonIdentifierType _createLP() {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current();
        final LegalPersonIdentifierType ret = new LegalPersonIdentifierType();
        ret.setLegalPersonIdentifier("LPI-ID-" + MathHelper.abs(aTLR.nextInt()));
        ret.setLegalName("LegalName-" + MathHelper.abs(aTLR.nextInt()));
        // Ignore the optional stuff
        return ret;
    }
    
    @Nonnull
    private static LegalPersonIdentifierType _createLP(String companyName, String company, String event) {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current();
        final LegalPersonIdentifierType ret = new LegalPersonIdentifierType();
        ret.setLegalPersonIdentifier(company);
        ret.setLegalName(companyName);
        // Ignore the optional stuff
        return ret;
    }

    @Nonnull
    public static DataRequestSubjectCVType _createDRS() {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current();
        final DataRequestSubjectCVType ret = new DataRequestSubjectCVType();
        if (aTLR.nextBoolean())
            ret.setDataSubjectPerson(_createNP());
        else {
            ret.setDataSubjectCompany(_createLP());
            if (aTLR.nextBoolean())
                ret.setDataSubjectRepresentative(_createNP());
        }
        return ret;
    }
    
    private static DataRequestSubjectCVType _createDRS(String companyName, String company, String event) {
    	final ThreadLocalRandom aTLR = ThreadLocalRandom.current();
        final DataRequestSubjectCVType ret = new DataRequestSubjectCVType();
        if (aTLR.nextBoolean())
            ret.setDataSubjectPerson(_createNP());
        else {
            ret.setDataSubjectCompany(_createLP(companyName,  company, event));
            if (aTLR.nextBoolean())
                ret.setDataSubjectRepresentative(_createNP());
        }
        return ret;
	}

    @Nonnull
    private static RequestGroundsType _createRequestGrounds() {
        final ThreadLocalRandom aTLR = ThreadLocalRandom.current();
        final RequestGroundsType ret = new RequestGroundsType();
        if (aTLR.nextBoolean())
            ret.setLawELIPermanentLink("https://example.org/article/" + MathHelper.abs(aTLR.nextInt()));
        else
            ret.setExplicitRequest(random(ExplicitRequestType.values()));
        return ret;
    }

	

}
