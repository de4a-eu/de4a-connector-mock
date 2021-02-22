package eu.de4a.connector.mock.config;

import eu.de4a.connector.mock.controller.MarshallException;
import eu.de4a.edm.jaxb.de_usi.ResponseForwardEvidenceType;
import eu.de4a.edm.jaxb.do_im.ResponseExtractEvidenceType;
import eu.de4a.edm.jaxb.dr_idk.ResponseLookupEvidenceServiceDataType;
import eu.de4a.edm.jaxb.dr_idk.ResponseLookupRoutingInformationType;
import eu.de4a.edm.jaxb.dr_usi.ResponseTransferEvidenceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

import static eu.de4a.edm.jaxb.do_im.ObjectFactory._ResponseExtractEvidence_QNAME;

@Configuration
@Slf4j
public class TestResponsesConfig {

    @Autowired
    Jaxb2Marshaller marshaller;
    @Autowired
    JaxbConfig.Helper helper;

    @Bean
    public eu.de4a.edm.jaxb.dr_im.ResponseTransferEvidenceType getDR1IMresponse() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/xml-schemas/examples/DR1-IM-response.xml");
        return helper.unmarshall(new StreamSource(xml.getInputStream()), eu.de4a.edm.jaxb.dr_im.ObjectFactory._ResponseTransferEvidence_QNAME);
    }

    @Bean
    public ResponseTransferEvidenceType DR1USIresponse() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/xml-schemas/examples/DR1-USI-response.xml");
        return helper.unmarshall(new StreamSource(xml.getInputStream()), eu.de4a.edm.jaxb.dr_usi.ObjectFactory._ResponseTransferEvidence_QNAME);
    }

    @Bean
    public eu.de4a.edm.jaxb.do_usi.ResponseExtractEvidenceType DO1USIresponse() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/xml-schemas/examples/DO1-USI-response.xml");
        return helper.unmarshall(new StreamSource(xml.getInputStream()), eu.de4a.edm.jaxb.do_usi.ObjectFactory._ResponseExtractEvidence_QNAME);
    }

    @Bean
    public ResponseForwardEvidenceType DE1USIresponse() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/xml-schemas/examples/DE1-USI-response.xml");
        return helper.unmarshall(new StreamSource(xml.getInputStream()), eu.de4a.edm.jaxb.de_usi.ObjectFactory._ResponseForwardEvidence_QNAME);
    }

    @Bean
    public ResponseExtractEvidenceType DO1IMresponse() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/xml-schemas/examples/DO1-IM-response.xml");
        return helper.unmarshall(new StreamSource(xml.getInputStream()), _ResponseExtractEvidence_QNAME);
    }

    @Bean
    public eu.de4a.edm.jaxb.dt_usi.ResponseTransferEvidenceType DT1USIresponse() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/xml-schemas/examples/DT1-USI-response.xml");
        return helper.unmarshall(new StreamSource(xml.getInputStream()), eu.de4a.edm.jaxb.dt_usi.ObjectFactory._ResponseTransferEvidence_QNAME);
    }

    @Bean
    public ResponseLookupEvidenceServiceDataType DR1IDKresponseevidence() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/xml-schemas/examples/DR1-IDK-response-evidence.xml");
        return helper.unmarshall(new StreamSource(xml.getInputStream()), eu.de4a.edm.jaxb.dr_idk.ObjectFactory._ResponseLookupEvidenceServiceData_QNAME);
    }

    @Bean
    public ResponseLookupRoutingInformationType DR1IDKresponserouting() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/xml-schemas/examples/DR1-IDK-response-routing.xml");
        return helper.unmarshall(new StreamSource(xml.getInputStream()), eu.de4a.edm.jaxb.dr_idk.ObjectFactory._ResponseLookupRoutingInformation_QNAME);
    }

}
