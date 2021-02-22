package eu.de4a.connector.mock.config;

import eu.de4a.connector.mock.controller.MarshallException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

@Configuration
@Slf4j
public class JaxbConfig {


    @Bean
    public MarshallingHttpMessageConverter marshallingHttpMessageConverter() throws IOException
    {
        MarshallingHttpMessageConverter marshallingHttpMessageConverter = new MarshallingHttpMessageConverter();

        marshallingHttpMessageConverter.setMarshaller(jaxb2Marshaller());
        marshallingHttpMessageConverter.setUnmarshaller(jaxb2Marshaller());

        return marshallingHttpMessageConverter;
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() throws IOException
    {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] schemas = resolver.getResources("/xml-schemas/XSD/*.xsd");
        String[] contextPaths = {
                "eu.de4a.edm.jaxb.de_usi",
                "eu.de4a.edm.jaxb.do_usi",
                "eu.de4a.edm.jaxb.do_im",
                "eu.de4a.edm.jaxb.dr_idk",
                "eu.de4a.edm.jaxb.dr_usi",
                "eu.de4a.edm.jaxb.dr_im",
                "eu.de4a.edm.jaxb.dt_usi",
        };
        log.info("schemas: {}",Arrays.stream(schemas).map(Resource::getFilename).reduce((s1, s2) -> String.format("%s, %s", s1, s2)).orElseGet(() -> "No schemas loaded"));
        jaxb2Marshaller.setContextPaths(contextPaths);
        jaxb2Marshaller.setSchemas(schemas);
        return jaxb2Marshaller;
    }

    @Bean
    public Helper getHelper(){
        return new Helper();
    }

    public class Helper {

        @Autowired
        Jaxb2Marshaller marshaller;

        public <T> T unmarshall(Source input, QName qName) throws MarshallException {
            JAXBElement<?> xml;
            try {
                xml = (JAXBElement<?>) marshaller.unmarshal(input);
            } catch (XmlMappingException ex) {
                throw new MarshallException("could not read message", ex);
            }
            if (xml.getName().equals(qName)) {
                return (T) xml.getValue();
            }
            throw new MarshallException(String.format("mismatched input expected [%s] got [%s]",
                    qName, xml.getName()));
        }

        public <T> String marshall(JAXBElement<T> input) throws MarshallException {
            StringWriter stringWriter = new StringWriter();
            Result res = new StreamResult(stringWriter);
            try {
                marshaller.marshal(input, res);
            } catch (XmlMappingException ex) {
                throw new MarshallException("could not marshall element", ex);
            }
            return stringWriter.toString();
        }

    }

}
