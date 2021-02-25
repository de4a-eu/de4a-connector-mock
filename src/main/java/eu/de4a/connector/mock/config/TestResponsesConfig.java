package eu.de4a.connector.mock.config;

import eu.de4a.connector.mock.controller.MarshallException;
import eu.de4a.edm.jaxb.de_usi.ResponseForwardEvidenceType;
import eu.de4a.edm.jaxb.do_im.ResponseExtractEvidenceType;
import eu.de4a.edm.jaxb.idk.ResponseLookupEvidenceServiceDataType;
import eu.de4a.edm.jaxb.idk.ResponseLookupRoutingInformationType;
import eu.de4a.edm.jaxb.dr_usi.ResponseTransferEvidenceType;
import eu.de4a.edm.xml.de4a.DE4AMarshaller;
import eu.de4a.edm.xml.de4a.IDE4ACanonicalEvidenceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;


@Configuration
@Slf4j
public class TestResponsesConfig {

    @Bean
    public eu.de4a.edm.jaxb.dr_im.ResponseTransferEvidenceType getDR1IMresponse() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("examples/T4.2-examples/DR1-IM-T42-response.xml");
        return DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.ALL_PREDEFINED).read(xml.getInputStream());
    }

    @Bean
    public ResponseTransferEvidenceType DR1USIresponse() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/examples/DR1-USI-response.xml");
        return DE4AMarshaller.drUsiResponseMarshaller().read(xml.getInputStream());
    }

    @Bean
    public eu.de4a.edm.jaxb.do_usi.ResponseExtractEvidenceType DO1USIresponse() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/examples/DO1-USI-response.xml");
        return DE4AMarshaller.doUsiResponseMarshaller().read(xml.getInputStream());
    }

    @Bean
    public ResponseForwardEvidenceType DE1USIresponse() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/examples/DE1-USI-response.xml");
        return DE4AMarshaller.deUsiResponseMarshaller().read(xml.getInputStream());
    }

    @Bean
    public ResponseExtractEvidenceType DO1IMresponse() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("examples/T4.2-examples/DO1-IM-T42-response.xml");
        return DE4AMarshaller.doImResponseMarshaller(IDE4ACanonicalEvidenceType.ALL_PREDEFINED).read(xml.getInputStream());
    }

    @Bean
    public eu.de4a.edm.jaxb.dt_usi.ResponseTransferEvidenceType DT1USIresponse() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/examples/DT1-USI-response.xml");
        return DE4AMarshaller.dtUsiResponseMarshaller().read(xml.getInputStream());
    }

    @Bean
    public ResponseLookupEvidenceServiceDataType DR1IDKresponseevidence() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/examples/DR-DT1-IDK-response-evidence.xml");
        return DE4AMarshaller.idkResponseLookupEvidenceServiceDataMarshaller().read(xml.getInputStream());
    }

    @Bean
    public ResponseLookupRoutingInformationType DR1IDKresponserouting() throws IOException, MarshallException {
        Resource xml = new ClassPathResource("/examples/DR-DT1-IDK-response-routing.xml");
        return DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller().read(xml.getInputStream());
    }

}
