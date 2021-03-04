package eu.de4a.connector.mock.config;

import eu.de4a.iem.jaxb.common.types.EvidenceServiceType;
import eu.de4a.iem.jaxb.common.types.IssuingAuthorityType;
import eu.de4a.iem.jaxb.t42.LegalEntityType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.t42.DE4AT42Marshaller;
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
    public IssuingAuthorityType getIssuingAuthorityType() throws IOException {
        Resource xml = new ClassPathResource("examples/DR-DT1-IDK-response-routing.xml");
        var res = DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller().read(xml.getInputStream());
        return res.getIssuingAuthority();
    }


    @Bean
    public EvidenceServiceType getEvidenceServiceType() throws IOException {
        Resource xml = new ClassPathResource("examples/DR-DT1-IDK-response-evidence.xml");
        var res = DE4AMarshaller.idkResponseLookupEvidenceServiceDataMarshaller().read(xml.getInputStream());
        return res.getEvidenceService();
    }
}
