package eu.de4a.connector.mock.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
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
        Resource[] schemas = resolver.getResources("/xml-schemas/XSD/**.xsd");
        String[] contextPaths = {
                "eu.de4a.jaxb.common.id",
                "eu.de4a.jaxb.common",
                "eu.de4a.jaxb.vocabularies.ccts",
                "eu.de4a.jaxb.vocabularies.codelist.mimemediatype",
                "eu.de4a.jaxb.vocabularies.codelist.country",
                "eu.de4a.jaxb.vocabularies.cv.ac",
                "eu.de4a.jaxb.vocabularies.cv.bc",
                "eu.de4a.jaxb.vocabularies.cv.a",
                "eu.de4a.jaxb.vocabularies.cv.cac",
                "eu.de4a.jaxb.vocabularies.cv.cbc",
                "eu.de4a.jaxb.vocabularies.cv.dt",
                "eu.de4a.jaxb.vocabularies.dc",
                "eu.de4a.jaxb.vocabularies.eidas.lp",
                "eu.de4a.jaxb.vocabularies.eidas.np",
                "eu.de4a.jaxb.vocabularies.foaf",
                "eu.de4a.jaxb.vocabularies.locn",
                "eu.de4a.jaxb.vocabularies.org",
                "eu.de4a.jaxb.vocabularies.rdf",
                "eu.de4a.jaxb.vocabularies.regorg",
                "eu.de4a.jaxb.vocabularies.skos",
                "eu.de4a.jaxb.vocabularies.ubl.udt",
                "eu.de4a.jaxb.de1.usi",
                "eu.de4a.jaxb.do1.im",
                "eu.de4a.jaxb.do1.usi",
                "eu.de4a.jaxb.dr1.idk",
                "eu.de4a.jaxb.dr1.im",
                "eu.de4a.jaxb.dr1.usi",
                "eu.de4a.jaxb.dt1.usi"
        };
        log.info("schemas: {}",Arrays.stream(schemas).map(Resource::getFilename).reduce((s1, s2) -> String.format("%s, %s", s1, s2)).orElseGet(() -> "No schemas loaded"));
        jaxb2Marshaller.setContextPaths(contextPaths);
        jaxb2Marshaller.setSchemas(schemas);
        return jaxb2Marshaller;
    }

}
