package eu.de4a.connector.mock;

import com.helger.jaxb.GenericJAXBMarshaller;
import eu.de4a.iem.xml.de4a.t42.v0_4.DE4AT42Marshaller;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

@Slf4j
public enum CanonicalEvidenceExamples {

    T42_SE("5591674170", "SE", new ClassPathResource("examples/T4.2-examples/sample company info SE -2.xml"),  DataOwner.V_SE, DE4AT42Marshaller.legalEntity()),
    T42_NL("90000471", "NL", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo NL KVK.xml"),  DataOwner.COC_NL, DE4AT42Marshaller.legalEntity()),
    T42_RO("12487", "RO", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo RO ONRC-2.xml"),  DataOwner.ONRC_RO, DE4AT42Marshaller.legalEntity());

    @Getter
    final private String registrationNumber;
    @Getter
    final private String country;
    @Getter
    final private Resource resource;
    @Getter
    final private DataOwner dataOwner;
    @Getter
    final private GenericJAXBMarshaller marshaller;
    private Element documentElement;
    private Pattern eIDASIdentifierPattern;

    private CanonicalEvidenceExamples(String registrationNumber, String country, Resource resource, DataOwner dataOwner, GenericJAXBMarshaller marshaller) {
        this.registrationNumber = registrationNumber;
        this.country = country;
        this.resource = resource;
        this.dataOwner = dataOwner;
        this.marshaller = marshaller;
        this.eIDASIdentifierPattern = Pattern.compile(String.format("^%s/[A-Z]{2}/%s$", country, registrationNumber));
    }

    public Element getDocumentElement() {
        if (documentElement == null) {
            try {
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
        return eIDASIdentifierPattern.matcher(eIDASIdentifier).find();
    }

    public static Element getDocumentElement(DataOwner dataOwner, String eIDASIdentifier) {
        return Arrays.stream(CanonicalEvidenceExamples.values())
                .filter(canonicalEvidenceExamples -> canonicalEvidenceExamples.dataOwner.equals(dataOwner))
                .filter(canonicalEvidenceExamples -> canonicalEvidenceExamples.isEIDASIdentifier(eIDASIdentifier))
                .findFirst()
                .map(CanonicalEvidenceExamples::getDocumentElement)
                .orElseGet(() -> null);
    }
}
