package eu.de4a.connector.mock;

import com.helger.jaxb.GenericJAXBMarshaller;
import eu.de4a.iem.xml.de4a.t42.v0_4.DE4AT42Marshaller;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.regex.Pattern;

public enum CanonicalEvidenceExamples {

    T42_SE("5591674170", "SE", new ClassPathResource("examples/T4.2-examples/sample company info SE -2.xml"), DE4AT42Marshaller.legalEntity()),
    T42_NL("90000471", "NL", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo NL KVK.xml"), DE4AT42Marshaller.legalEntity()),
    T42_RO("12487", "RO", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo RO ONRC-2.xml"), DE4AT42Marshaller.legalEntity());

    @Getter
    final private String registrationNumber;
    @Getter
    final private String country;
    @Getter
    final private Resource resource;
    @Getter
    final private GenericJAXBMarshaller marshaller;
    private Element documentElement;
    private Pattern eIDASLegalIdentifierPattern;

    private CanonicalEvidenceExamples(String registrationNumber, String country, Resource resource, GenericJAXBMarshaller marshaller) {
        this.registrationNumber = registrationNumber;
        this.country = country;
        this.resource = resource;
        this.marshaller = marshaller;
        this.eIDASLegalIdentifierPattern = Pattern.compile(String.format("^%s/[A-Z]{2}/%s$", country, registrationNumber));
    }

    public Element getDocumentElement() throws IOException {
        if (documentElement == null) {
            documentElement = marshaller.getAsDocument(marshaller.read(resource.getInputStream())).getDocumentElement();
        }
        return documentElement;
    }

    public boolean isEIDASLegalIdentifier(String eIDASLegalIdentifier) {
        return eIDASLegalIdentifierPattern.matcher(eIDASLegalIdentifier).find();
    }
}
