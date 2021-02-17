package eu.de4a.connector.mock.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import eu.de4a.jaxb.de1.usi.ResponseForwardEvidence;
import eu.de4a.jaxb.do1.usi.ResponseExtractEvidence;
import eu.de4a.jaxb.dr1.idk.ResponseLookupEvidenceServiceData;
import eu.de4a.jaxb.dr1.idk.ResponseLookupRoutingInformation;
import eu.de4a.jaxb.dr1.im.ResponseTransferEvidence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@Configuration
@Slf4j
public class TestResponsesConfig {

    @Autowired
    Jaxb2Marshaller marshaller;

    @Bean
    public ResponseTransferEvidence getDR1IMresponse() throws IOException, JAXBException {
        Resource xml = new ClassPathResource("/xml-schemas/XSD/examples/DR1-IM-response.xml");
        var resp = (ResponseTransferEvidence) marshaller.createUnmarshaller().unmarshal(xml.getInputStream());
        log.debug("dr1im response test data: {}", ToStringBuilder.reflectionToString(resp));
        return resp;
    }

    @Bean
    public eu.de4a.jaxb.dr1.usi.ResponseTransferEvidence DR1USIresponse() throws IOException, JAXBException {
        Resource xml = new ClassPathResource("/xml-schemas/XSD/examples/DR1-USI-response.xml");
        var resp = (eu.de4a.jaxb.dr1.usi.ResponseTransferEvidence) marshaller.createUnmarshaller().unmarshal(xml.getInputStream());
        log.debug("dr1usi response test data: {}", ToStringBuilder.reflectionToString(resp));
        return resp;
    }

    @Bean
    public ResponseExtractEvidence DO1USIresponse() throws IOException, JAXBException {
        Resource xml = new ClassPathResource("/xml-schemas/XSD/examples/DO1-USI-response.xml");
        var resp = (ResponseExtractEvidence) marshaller.createUnmarshaller().unmarshal(xml.getInputStream());
        log.debug("do1usi response test data: {}", ToStringBuilder.reflectionToString(resp));
        return resp;
    }

    @Bean
    public ResponseForwardEvidence DE1USIresponse() throws IOException, JAXBException {
        Resource xml = new ClassPathResource("/xml-schemas/XSD/examples/DE1-USI-response.xml");
        var resp = (ResponseForwardEvidence) marshaller.createUnmarshaller().unmarshal(xml.getInputStream());
        log.debug("de1usi response test data: {}", ToStringBuilder.reflectionToString(resp));
        return resp;
    }

    @Bean
    public eu.de4a.jaxb.do1.im.ResponseExtractEvidence DO1IMresponse() throws IOException, JAXBException {
        Resource xml = new ClassPathResource("/xml-schemas/XSD/examples/DO1-IM-response.xml");
        var resp = (eu.de4a.jaxb.do1.im.ResponseExtractEvidence) marshaller.createUnmarshaller().unmarshal(xml.getInputStream());
        log.debug("do1im response test data: {}", ToStringBuilder.reflectionToString(resp));
        return resp;
    }

    @Bean
    public eu.de4a.jaxb.dt1.usi.ResponseTransferEvidence DT1USIresponse() throws IOException, JAXBException {
        Resource xml = new ClassPathResource("/xml-schemas/XSD/examples/DT1-USI-response.xml");
        var resp = (eu.de4a.jaxb.dt1.usi.ResponseTransferEvidence) marshaller.createUnmarshaller().unmarshal(xml.getInputStream());
        log.debug("dt1usi response test data: {}", ToStringBuilder.reflectionToString(resp));
        return resp;
    }

    @Bean
    public ResponseLookupEvidenceServiceData DR1IDKresponseevidence() throws IOException, JAXBException {
        Resource xml = new ClassPathResource("/xml-schemas/XSD/examples/DR1-IDK-response-evidence.xml");
        var resp = (ResponseLookupEvidenceServiceData) marshaller.createUnmarshaller().unmarshal(xml.getInputStream());
        log.debug("dr1idkevidence response test data: {}", ToStringBuilder.reflectionToString(resp));
        return resp;
    }

    @Bean
    public ResponseLookupRoutingInformation DR1IDKresponserouting() throws IOException, JAXBException {
        Resource xml = new ClassPathResource("/xml-schemas/XSD/examples/DR1-IDK-response-routing.xml");
        var resp = (ResponseLookupRoutingInformation) marshaller.createUnmarshaller().unmarshal(xml.getInputStream());
        log.debug("dr1idkrouting response test data: {}", ToStringBuilder.reflectionToString(resp));
        return resp;
    }


}
