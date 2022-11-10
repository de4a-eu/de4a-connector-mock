package eu.de4a.connector.mock.exampledata;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import com.helger.jaxb.GenericJAXBMarshaller;

import eu.de4a.iem.cev.de4a.t41.DE4AT41Marshaller;
import eu.de4a.iem.cev.de4a.t42.DE4AT42Marshaller;
import eu.de4a.iem.cev.de4a.t43.DE4AT43Marshaller;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
public enum CanonicalEventSubscriptionExamples {

    T43_M_PT("12345678", new ClassPathResource("examples/T4.3-examples/MA-example-Marriage-PT.xml"), DataOwner.AMA_PT, SubscriptionID.MARRIAGE_EVIDENCE, DE4AT43Marshaller.marriageEvidence(), USIAutoResponse.OFF),
    T43_M_PT_A("12345678A", new ClassPathResource("examples/T4.3-examples/MA-example-Marriage-PT-A.xml"), DataOwner.AMA_PT, SubscriptionID.MARRIAGE_EVIDENCE, DE4AT43Marshaller.marriageEvidence(), USIAutoResponse.IMMEDIATE),
    T43_M_PT_B("12345678B", new ClassPathResource("examples/T4.3-examples/MA-example-Marriage-PT-B.xml"), DataOwner.AMA_PT, SubscriptionID.MARRIAGE_EVIDENCE, DE4AT43Marshaller.marriageEvidence(), USIAutoResponse.DELAY_5_SEC),
    T43_M_PT_C("12345678C", new ClassPathResource("examples/T4.3-examples/MA-example-Marriage-PT-C.xml"), DataOwner.AMA_PT, SubscriptionID.MARRIAGE_EVIDENCE, DE4AT43Marshaller.marriageEvidence(), USIAutoResponse.DELAY_40_SEC),
    T43_M_PT_D("12345678D", new ClassPathResource("examples/T4.3-examples/MA-example-Marriage-PT-D.xml"), DataOwner.AMA_PT, SubscriptionID.MARRIAGE_EVIDENCE, DE4AT43Marshaller.marriageEvidence(), USIAutoResponse.DELAY_240_SEC),
    T43_DR_PT("12345678", new ClassPathResource("examples/T4.3-examples/MA-example-DomicileRegistration-PT.xml"), DataOwner.AMA_PT, SubscriptionID.DOMICILE_REGISTRATION_EVIDENCE, DE4AT43Marshaller.domicileRegistrationEvidence(), USIAutoResponse.OFF),
    T43_DR_PT_A("12345678A", new ClassPathResource("examples/T4.3-examples/MA-example-DomicileRegistration-PT-A.xml"), DataOwner.AMA_PT, SubscriptionID.DOMICILE_REGISTRATION_EVIDENCE, DE4AT43Marshaller.domicileRegistrationEvidence(), USIAutoResponse.IMMEDIATE),
    T43_DR_PT_B("12345678B", new ClassPathResource("examples/T4.3-examples/MA-example-DomicileRegistration-PT-B.xml"), DataOwner.AMA_PT, SubscriptionID.DOMICILE_REGISTRATION_EVIDENCE, DE4AT43Marshaller.domicileRegistrationEvidence(), USIAutoResponse.DELAY_5_SEC),
    T43_DR_PT_C("12345678C", new ClassPathResource("examples/T4.3-examples/MA-example-DomicileRegistration-PT-C.xml"), DataOwner.AMA_PT, SubscriptionID.DOMICILE_REGISTRATION_EVIDENCE, DE4AT43Marshaller.domicileRegistrationEvidence(), USIAutoResponse.DELAY_40_SEC),
    T43_DR_PT_D("12345678D", new ClassPathResource("examples/T4.3-examples/MA-example-DomicileRegistration-PT-D.xml"), DataOwner.AMA_PT, SubscriptionID.DOMICILE_REGISTRATION_EVIDENCE, DE4AT43Marshaller.domicileRegistrationEvidence(), USIAutoResponse.DELAY_240_SEC),
    T43_B_PT("12345678", new ClassPathResource("examples/T4.3-examples/MA-example-Birth-PT.xml"), DataOwner.AMA_PT, SubscriptionID.BIRTH_EVIDENCE, DE4AT43Marshaller.birthEvidence(), USIAutoResponse.OFF),
    T43_B_PT_A("12345678A", new ClassPathResource("examples/T4.3-examples/MA-example-Birth-PT-A.xml"), DataOwner.AMA_PT, SubscriptionID.BIRTH_EVIDENCE, DE4AT43Marshaller.birthEvidence(), USIAutoResponse.IMMEDIATE),
    T43_B_PT_B("12345678B", new ClassPathResource("examples/T4.3-examples/MA-example-Birth-PT-B.xml"), DataOwner.AMA_PT, SubscriptionID.BIRTH_EVIDENCE, DE4AT43Marshaller.birthEvidence(), USIAutoResponse.DELAY_5_SEC),
    T43_B_PT_C("12345678C", new ClassPathResource("examples/T4.3-examples/MA-example-Birth-PT-C.xml"), DataOwner.AMA_PT, SubscriptionID.BIRTH_EVIDENCE, DE4AT43Marshaller.birthEvidence(), USIAutoResponse.DELAY_40_SEC),
    T43_B_PT_D("12345678D", new ClassPathResource("examples/T4.3-examples/MA-example-Birth-PT-D.xml"), DataOwner.AMA_PT, SubscriptionID.BIRTH_EVIDENCE, DE4AT43Marshaller.birthEvidence(), USIAutoResponse.DELAY_240_SEC),
    T43_DR_ES("99999142H", new ClassPathResource("examples/T4.3-examples/MA-example-DomicileRegistration-ES.xml"), DataOwner.SGAD_ES, SubscriptionID.DOMICILE_REGISTRATION_EVIDENCE, DE4AT43Marshaller.domicileRegistrationEvidence(), USIAutoResponse.OFF),
    T43_DR_ES_A("99999142HA", new ClassPathResource("examples/T4.3-examples/MA-example-DomicileRegistration-ES-A.xml"), DataOwner.SGAD_ES, SubscriptionID.DOMICILE_REGISTRATION_EVIDENCE, DE4AT43Marshaller.domicileRegistrationEvidence(), USIAutoResponse.IMMEDIATE),
    T43_DR_ES_B("99999142HB", new ClassPathResource("examples/T4.3-examples/MA-example-DomicileRegistration-ES-B.xml"), DataOwner.SGAD_ES, SubscriptionID.DOMICILE_REGISTRATION_EVIDENCE, DE4AT43Marshaller.domicileRegistrationEvidence(), USIAutoResponse.DELAY_5_SEC),
    T43_DR_ES_C("99999142HC", new ClassPathResource("examples/T4.3-examples/MA-example-DomicileRegistration-ES-C.xml"), DataOwner.SGAD_ES, SubscriptionID.DOMICILE_REGISTRATION_EVIDENCE, DE4AT43Marshaller.domicileRegistrationEvidence(), USIAutoResponse.DELAY_40_SEC),
    T43_DR_ES_D("99999142HD", new ClassPathResource("examples/T4.3-examples/MA-example-DomicileRegistration-ES-D.xml"), DataOwner.SGAD_ES, SubscriptionID.DOMICILE_REGISTRATION_EVIDENCE, DE4AT43Marshaller.domicileRegistrationEvidence(), USIAutoResponse.DELAY_240_SEC),
    T43_B_ES("99999142H", new ClassPathResource("examples/T4.3-examples/MA-example-Birth-ES.xml"), DataOwner.SGAD_ES, SubscriptionID.BIRTH_EVIDENCE, DE4AT43Marshaller.birthEvidence(), USIAutoResponse.OFF),
    T43_B_ES_A("99999142HA", new ClassPathResource("examples/T4.3-examples/MA-example-Birth-ES-A.xml"), DataOwner.SGAD_ES, SubscriptionID.BIRTH_EVIDENCE, DE4AT43Marshaller.birthEvidence(), USIAutoResponse.IMMEDIATE),
    T43_B_ES_B("99999142HB", new ClassPathResource("examples/T4.3-examples/MA-example-Birth-ES-B.xml"), DataOwner.SGAD_ES, SubscriptionID.BIRTH_EVIDENCE, DE4AT43Marshaller.birthEvidence(), USIAutoResponse.DELAY_5_SEC),
    T43_B_ES_C("99999142HC", new ClassPathResource("examples/T4.3-examples/MA-example-Birth-ES-C.xml"), DataOwner.SGAD_ES, SubscriptionID.BIRTH_EVIDENCE, DE4AT43Marshaller.birthEvidence(), USIAutoResponse.DELAY_40_SEC),
    T43_B_ES_D("99999142HD", new ClassPathResource("examples/T4.3-examples/MA-example-Birth-ES-D.xml"), DataOwner.SGAD_ES, SubscriptionID.BIRTH_EVIDENCE, DE4AT43Marshaller.birthEvidence(), USIAutoResponse.DELAY_240_SEC),
    T43_M_ES("99999142H", new ClassPathResource("examples/T4.3-examples/MA-example-Marriage-ES.xml"), DataOwner.SGAD_ES, SubscriptionID.MARRIAGE_EVIDENCE, DE4AT43Marshaller.marriageEvidence(), USIAutoResponse.OFF),
    T43_M_ES_A("99999142HA", new ClassPathResource("examples/T4.3-examples/MA-example-Marriage-ES-A.xml"), DataOwner.SGAD_ES, SubscriptionID.MARRIAGE_EVIDENCE, DE4AT43Marshaller.marriageEvidence(), USIAutoResponse.IMMEDIATE),
    T43_M_ES_B("99999142HB", new ClassPathResource("examples/T4.3-examples/MA-example-Marriage-ES-B.xml"), DataOwner.SGAD_ES, SubscriptionID.MARRIAGE_EVIDENCE, DE4AT43Marshaller.marriageEvidence(), USIAutoResponse.DELAY_5_SEC),
    T43_M_ES_C("99999142HC", new ClassPathResource("examples/T4.3-examples/MA-example-Marriage-ES-C.xml"), DataOwner.SGAD_ES, SubscriptionID.MARRIAGE_EVIDENCE, DE4AT43Marshaller.marriageEvidence(), USIAutoResponse.DELAY_40_SEC),
    T43_M_ES_D("99999142HD", new ClassPathResource("examples/T4.3-examples/MA-example-Marriage-ES-D.xml"), DataOwner.SGAD_ES, SubscriptionID.MARRIAGE_EVIDENCE, DE4AT43Marshaller.marriageEvidence(), USIAutoResponse.DELAY_240_SEC),
    T42_SE("5591674170", new ClassPathResource("examples/T4.2-examples/sample company info SE -2.xml"),  DataOwner.V_SE, SubscriptionID.COMPANY_REGISTRATION, DE4AT42Marshaller.legalEntity(), USIAutoResponse.OFF),
    T42_NL("90000471", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo NL KVK.xml"),  DataOwner.COC_NL, SubscriptionID.COMPANY_REGISTRATION, DE4AT42Marshaller.legalEntity(), USIAutoResponse.OFF),
    T42_RO("J40/12487/1998", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo RO ONRC-2.xml"),  DataOwner.ONRC_RO, SubscriptionID.COMPANY_REGISTRATION, DE4AT42Marshaller.legalEntity(),  USIAutoResponse.OFF),
    T42_AT("???", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo AT.xml"),  DataOwner.DMDW_AT, SubscriptionID.COMPANY_REGISTRATION, DE4AT42Marshaller.legalEntity(), USIAutoResponse.OFF),
    
    T42_AT_BE("???", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo AT.xml"),  DataOwner.DMDW_AT, SubscriptionID.BUSINESS_EVENTS, DE4AT42Marshaller.legalEntity(), USIAutoResponse.OFF),
    
    T41_PT("123456789", new ClassPathResource("examples/T4.1-examples/SA-UC1-11-02-2021-example-PT.xml"), DataOwner.UL_PT, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.OFF),
    T41_SI("123456", new ClassPathResource("examples/T4.1-examples/SA-UC1-example-SI.xml"), DataOwner.MIZS_SI, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.OFF),
    T41_ES("53377873W", new ClassPathResource("examples/T4.1-examples/SA-UC1-example-ES.xml"), DataOwner.SGAD_ES, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.OFF),
    T41_PT_A("123456789A", new ClassPathResource("examples/T4.1-examples/SA-UC1-11-02-2021-example-PT_A.xml"), DataOwner.UL_PT, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.IMMEDIATE),
    T41_SI_A("123456A", new ClassPathResource("examples/T4.1-examples/SA-UC1-example-SI_A.xml"), DataOwner.MIZS_SI, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.IMMEDIATE),
    T41_ES_A("53377873WA", new ClassPathResource("examples/T4.1-examples/SA-UC1-example-ES_A.xml"), DataOwner.SGAD_ES, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.IMMEDIATE),
    T41_PT_B("123456789B", new ClassPathResource("examples/T4.1-examples/SA-UC1-11-02-2021-example-PT_B.xml"), DataOwner.UL_PT, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.DELAY_5_SEC),
    T41_SI_B("123456B", new ClassPathResource("examples/T4.1-examples/SA-UC1-example-SI_B.xml"), DataOwner.MIZS_SI, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.DELAY_5_SEC),
    T41_ES_B("53377873WB", new ClassPathResource("examples/T4.1-examples/SA-UC1-example-ES_B.xml"), DataOwner.SGAD_ES, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.DELAY_5_SEC),
    T41_PT_C("123456789C", new ClassPathResource("examples/T4.1-examples/SA-UC1-11-02-2021-example-PT_C.xml"), DataOwner.UL_PT, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.DELAY_40_SEC),
    T41_SI_C("123456C", new ClassPathResource("examples/T4.1-examples/SA-UC1-example-SI_C.xml"), DataOwner.MIZS_SI, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.DELAY_40_SEC),
    T41_ES_C("53377873WC", new ClassPathResource("examples/T4.1-examples/SA-UC1-example-ES_C.xml"), DataOwner.SGAD_ES, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.DELAY_40_SEC),
    T41_PT_D("123456789D", new ClassPathResource("examples/T4.1-examples/SA-UC1-11-02-2021-example-PT_D.xml"), DataOwner.UL_PT, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.DELAY_240_SEC),
    T41_SI_D("123456D", new ClassPathResource("examples/T4.1-examples/SA-UC1-example-SI_D.xml"), DataOwner.MIZS_SI, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.DELAY_240_SEC),
    T41_ES_D("53377873WD", new ClassPathResource("examples/T4.1-examples/SA-UC1-example-ES_D.xml"), DataOwner.UJI_ES, SubscriptionID.HIGHER_EDUCATION_DIPLOMA, DE4AT41Marshaller.higherEducationDiploma(), USIAutoResponse.DELAY_240_SEC);

    @Getter
    private final String identifier;
    @Getter
    private final Resource resource;
    @Getter
    private final DataOwner dataOwner;
    @Getter
    private final SubscriptionID subscriptionID;
    @Getter
    @ToString.Exclude
    private final GenericJAXBMarshaller marshaller;
    @Getter
    private final USIAutoResponse usiAutoResponse;
    @ToString.Exclude
    private Element documentElement;
    private final Pattern eIDASIdentifierPattern;

    private CanonicalEventSubscriptionExamples(String identifier,
                                      Resource resource,
                                      DataOwner dataOwner,
                                      SubscriptionID subscriptionID,
                                      GenericJAXBMarshaller marshaller,
                                      USIAutoResponse usiAutoResponse
    ) {
        this.identifier = identifier;
        this.resource = resource;
        this.dataOwner = dataOwner;
        this.subscriptionID = subscriptionID;
        this.marshaller = marshaller;
        this.usiAutoResponse = usiAutoResponse;
        this.eIDASIdentifierPattern = Pattern.compile(String.format("^%s/[A-Z]{2}/%s$", Pattern.quote(dataOwner.getCountry().toUpperCase(Locale.ROOT)), Pattern.quote(identifier.toUpperCase(Locale.ROOT))));
    }

    public Element getDocumentElement() {
        if (documentElement == null) {
            try { // this is tested for all CanonicalEvidences in CanonicalEvidenceExamplesTest. It should not ever fail.
                documentElement = marshaller.getAsDocument(marshaller.read(resource.getInputStream())).getDocumentElement();
            } catch (IOException ex) {
                log.error("resource {} can't be marshalled, {}", resource.getFilename(), ex.getMessage());
                ex.printStackTrace();
                return null;
            }
        }
        return documentElement;
    }

    public boolean isEIDASIdentifier(String eIDASIdentifier) {
        log.debug("Pattern: {}", eIDASIdentifierPattern.pattern());
        return eIDASIdentifierPattern.matcher(eIDASIdentifier).find();
    }

    private static Stream<CanonicalEventSubscriptionExamples> getCanonicalEventSubscriptionStream(DataOwner dataOwner, SubscriptionID subscriptionID, String eIDASIdentifier) {
        return Arrays.stream(CanonicalEventSubscriptionExamples.values())
                .filter(canonicalEventSubscriptionExamples -> canonicalEventSubscriptionExamples.dataOwner.equals(dataOwner))
                .filter(canonicalEventSubscriptionExamples -> canonicalEventSubscriptionExamples.subscriptionID.equals(subscriptionID))
                .filter(canonicalEventSubscriptionExamples -> canonicalEventSubscriptionExamples.isEIDASIdentifier(eIDASIdentifier.toUpperCase(Locale.ROOT)));
    }

    public static CanonicalEventSubscriptionExamples getCanonicalEventSubscription(DataOwner dataOwner, SubscriptionID subscriptionID, String eIDASIdentifier) {
        return CanonicalEventSubscriptionExamples.getCanonicalEventSubscriptionStream(dataOwner, subscriptionID, eIDASIdentifier)
                .findFirst()
                .orElseGet(() -> null);
    }

    public static Element getDocumentElement(DataOwner dataOwner, SubscriptionID subscriptionID, String eIDASIdentifier) {
        return CanonicalEventSubscriptionExamples.getCanonicalEventSubscriptionStream(dataOwner, subscriptionID, eIDASIdentifier)
                .findFirst()
                .map(CanonicalEventSubscriptionExamples::getDocumentElement)
                .orElseGet(() -> null);
    }
}
