package eu.de4a.connector.mock;

import com.helger.jaxb.GenericJAXBMarshaller;
import eu.de4a.iem.xml.de4a.t42.v0_4.DE4AT42Marshaller;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import java.io.IOException;

public enum CannonicalEvidenceExamples {

    T42_SE("SEBOLREG.5591674170", new ClassPathResource("examples/T4.2-examples/sample company info SE -2.xml"), DE4AT42Marshaller.legalEntity()),
    T42_NL("NLNHR.90000471", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo NL KVK.xml"), DE4AT42Marshaller.legalEntity()),
    T42_RO("ROONRC.J40/12487/1998", new ClassPathResource("examples/T4.2-examples/sample CompanyInfo RO ONRC-2.xml"), DE4AT42Marshaller.legalEntity());

    @Getter
    final private String EUID;
    @Getter
    final private Resource resource;
    @Getter
    final private GenericJAXBMarshaller marshaller;
    private Element documentElement;

    private CannonicalEvidenceExamples(String EUID, Resource resource, GenericJAXBMarshaller marshaller) {
        this.EUID = EUID;
        this.resource = resource;
        this.marshaller = marshaller;
    }

    public Element getDocumentElement() throws IOException {
        if (documentElement == null) {
            documentElement = marshaller.getAsDocument(marshaller.read(resource.getInputStream())).getDocumentElement();
        }
        return documentElement;
    }
}
