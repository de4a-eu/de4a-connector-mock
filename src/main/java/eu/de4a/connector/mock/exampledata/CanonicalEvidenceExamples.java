package eu.de4a.connector.mock.exampledata;

import com.helger.jaxb.GenericJAXBMarshaller;
import eu.de4a.iem.xml.de4a.t42.v0_6.DE4AT42Marshaller;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public enum CanonicalEvidenceExamples {

    T42_SE("5591674170", new ClassPathResource("examples/T4.2-examples/sample company info SE -2.xml"),  DataOwner.V_SE, EvidenceID.COMPANY_REGISTRATION, DE4AT42Marshaller.legalEntity(), USIAutoResponse.OFF),
    T42_NL("90000471", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo NL KVK.xml"),  DataOwner.COC_NL, EvidenceID.COMPANY_REGISTRATION, DE4AT42Marshaller.legalEntity(), USIAutoResponse.IMMEDIATE),
    T42_RO("J40/12487/1998", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo RO ONRC-2.xml"),  DataOwner.ONRC_RO, EvidenceID.COMPANY_REGISTRATION, DE4AT42Marshaller.legalEntity(),  USIAutoResponse.DELAY_5_SEC),
    T42_AT("???", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo AT.xml"),  DataOwner.DMDW_AT, EvidenceID.COMPANY_REGISTRATION, DE4AT42Marshaller.legalEntity(), USIAutoResponse.OFF);

    @Getter
    final private String identifier;
    @Getter
    final private Resource resource;
    @Getter
    final private DataOwner dataOwner;
    @Getter
    final private EvidenceID evidenceID;
    @Getter
    final private GenericJAXBMarshaller marshaller;
    @Getter
    final private USIAutoResponse usiAutoResponse;
    private Element documentElement;
    private final Pattern eIDASIdentifierPattern;

    private CanonicalEvidenceExamples(String identifier,
                                      Resource resource,
                                      DataOwner dataOwner,
                                      EvidenceID evidenceID,
                                      GenericJAXBMarshaller marshaller,
                                      USIAutoResponse usiAutoResponse
    ) {
        this.identifier = identifier;
        this.resource = resource;
        this.dataOwner = dataOwner;
        this.evidenceID = evidenceID;
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

    private static Stream<CanonicalEvidenceExamples> getCanonicalEvidenceStream(DataOwner dataOwner, EvidenceID evidenceID, String eIDASIdentifier) {
        return Arrays.stream(CanonicalEvidenceExamples.values())
                .filter(canonicalEvidenceExamples -> canonicalEvidenceExamples.dataOwner.equals(dataOwner))
                .filter(canonicalEvidenceExamples -> canonicalEvidenceExamples.evidenceID.equals(evidenceID))
                .filter(canonicalEvidenceExamples -> canonicalEvidenceExamples.isEIDASIdentifier(eIDASIdentifier.toUpperCase(Locale.ROOT)));
    }

    public static CanonicalEvidenceExamples getCanonicalEvidence(DataOwner dataOwner, EvidenceID evidenceID, String eIDASIdentifier) {
        return CanonicalEvidenceExamples.getCanonicalEvidenceStream(dataOwner, evidenceID, eIDASIdentifier)
                .findFirst()
                .orElseGet(() -> null);
    }

    public static Element getDocumentElement(DataOwner dataOwner, EvidenceID evidenceID, String eIDASIdentifier) {
        return CanonicalEvidenceExamples.getCanonicalEvidenceStream(dataOwner, evidenceID, eIDASIdentifier)
                .findFirst()
                .map(CanonicalEvidenceExamples::getDocumentElement)
                .orElseGet(() -> null);
    }
}
