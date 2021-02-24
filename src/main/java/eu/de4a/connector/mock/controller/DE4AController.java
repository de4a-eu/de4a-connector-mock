package eu.de4a.connector.mock.controller;

import eu.de4a.edm.jaxb.de_usi.RequestForwardEvidenceType;
import eu.de4a.edm.jaxb.de_usi.ResponseForwardEvidenceType;
import eu.de4a.edm.jaxb.do_im.RequestExtractEvidenceType;
import eu.de4a.edm.jaxb.do_im.ResponseExtractEvidenceType;
import eu.de4a.edm.jaxb.idk.RequestLookupEvidenceServiceDataType;
import eu.de4a.edm.jaxb.idk.RequestLookupRoutingInformationType;
import eu.de4a.edm.jaxb.idk.ResponseLookupEvidenceServiceDataType;
import eu.de4a.edm.jaxb.idk.ResponseLookupRoutingInformationType;
import eu.de4a.edm.jaxb.dr_im.RequestTransferEvidenceType;
import eu.de4a.edm.jaxb.dr_im.ResponseTransferEvidenceType;
import eu.de4a.edm.xml.de4a.DE4AMarshaller;
import eu.de4a.edm.xml.de4a.EDE4ACanonicalEvidenceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
public class DE4AController {

    @Autowired
    ResponseExtractEvidenceType do1imresp;
    @Autowired
    eu.de4a.edm.jaxb.do_usi.ResponseExtractEvidenceType do1usiresp;
    @Autowired
    ResponseForwardEvidenceType de1usiresp;
    @Autowired
    ResponseLookupEvidenceServiceDataType dr1idkevidenceresp;
    @Autowired
    ResponseLookupRoutingInformationType dr1idkroutingresp;
    @Autowired
    ResponseTransferEvidenceType dr1imresp;
    @Autowired
    eu.de4a.edm.jaxb.dr_usi.ResponseTransferEvidenceType dr1usiresp;
    @Autowired
    eu.de4a.edm.jaxb.dt_usi.ResponseTransferEvidenceType dt1usiresp;


    @PostMapping("/do1/im/extractevidence")
    public ResponseEntity<String> DO1ImRequestExtractEvidence(InputStream body) {
        var marshaller = DE4AMarshaller.doImRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestExtractEvidenceType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO).getAsString(do1imresp));
    }

    @PostMapping("/do1/usi/extractevidence")
    public ResponseEntity<String> DO1USIRequestExtractEvidence(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.doUsiRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        eu.de4a.edm.jaxb.do_usi.RequestExtractEvidenceType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(do1usiresp));
    }

    @PostMapping("/de1/usi/forwardevidence")
    public ResponseEntity<String> de1usiresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.deUsiRequestMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO);
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestForwardEvidenceType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.deUsiResponseMarshaller().getAsString(de1usiresp));
    }

    @PostMapping("/dr1/idk/lookupevidenceservicedata")
    public ResponseEntity<String> dr1idkevidenceresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.idkRequestLookupEvidenceServiceDataMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestLookupEvidenceServiceDataType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.idkResponseLookupEvidenceServiceDataMarshaller().getAsString(dr1idkevidenceresp));
    }

    @PostMapping("/dr1/idk/lookuproutinginformation")
    public ResponseEntity<String> dr1idkroutingresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.idkRequestLookupRoutingInformationMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestLookupRoutingInformationType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller().getAsString(dr1idkroutingresp));
    }

    @PostMapping("/dr1/im/transferevidence")
    public ResponseEntity<String> dr1imresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.drImRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestTransferEvidenceType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO).getAsString(dr1imresp));
    }

    @PostMapping("/dr1/usi/transferevidence")
    public ResponseEntity<String> dr1usiresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.drUsiRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        eu.de4a.edm.jaxb.dr_usi.RequestTransferEvidenceType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drUsiResponseMarshaller().getAsString(dr1usiresp));
    }

    @PostMapping("/dt1/usi/transferevidence")
    public ResponseEntity<String> dt1usiresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.dtUsiRequestMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO);
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        eu.de4a.edm.jaxb.dt_usi.RequestTransferEvidenceType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.dtUsiResponseMarshaller().getAsString(dt1usiresp));
    }

}
