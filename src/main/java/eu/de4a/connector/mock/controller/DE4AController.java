package eu.de4a.connector.mock.controller;

import eu.de4a.edm.jaxb.common.types.*;
import eu.de4a.edm.jaxb.t42.LegalEntityType;
import eu.de4a.edm.xml.de4a.DE4AMarshaller;
import eu.de4a.edm.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.edm.xml.de4a.EDE4ACanonicalEvidenceType;
import eu.de4a.edm.xml.de4a.t42.DE4AT42Marshaller;
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
    LegalEntityType t42Evidence;
    @Autowired
    EvidenceServiceType evidenceServiceType;
    @Autowired
    IssuingAuthorityType issuingAuthorityType;

    @PostMapping("/do1/im/extractevidence")
    public ResponseEntity<String> DO1ImRequestExtractEvidence(InputStream body) {
        var marshaller = DE4AMarshaller.doImRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> MarshallErrorHandler.getInstance().postError(errorKey, ex));
        RequestExtractEvidenceIMType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        var res = DE4AResponseDocumentHelper.createResponseExtractEvidence(req);
        CanonicalEvidenceType ce = new CanonicalEvidenceType();
        ce.setAny(DE4AT42Marshaller.legalEntity().getAsDocument(t42Evidence).getDocumentElement());
        res.setCanonicalEvidence(ce);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(EDE4ACanonicalEvidenceType.ALL_PREDEFINED).getAsString(res));
    }

    @PostMapping("/do1/usi/extractevidence")
    public ResponseEntity<String> DO1USIRequestExtractEvidence(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.doUsiRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestExtractEvidenceUSIType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        ResponseErrorType res = DE4AResponseDocumentHelper.createResponseError(true);
        res.setErrorList(new ErrorListType());
        ErrorType error = new ErrorType();
        error.setCode("asdf");
        error.setText("asdf");
        res.getErrorList().getError().add(error);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(res));
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
        ResponseErrorType res = DE4AResponseDocumentHelper.createResponseError(true);
        res.setErrorList(new ErrorListType());
        ErrorType error = new ErrorType();
        error.setCode("asdf");
        error.setText("asdf");
        res.getErrorList().getError().add(error);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.deUsiResponseMarshaller().getAsString(res));
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
        ResponseLookupEvidenceServiceDataType res = new ResponseLookupEvidenceServiceDataType();
        res.setEvidenceService(evidenceServiceType);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.idkResponseLookupEvidenceServiceDataMarshaller().getAsString(res));
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
        ResponseLookupRoutingInformationType res = new ResponseLookupRoutingInformationType();
        res.setIssuingAuthority(issuingAuthorityType);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller().getAsString(res));
    }

    @PostMapping("/dr1/im/transferevidence")
    public ResponseEntity<String> dr1imresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.drImRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestTransferEvidenceIMType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        var res = DE4AResponseDocumentHelper.createResponseTransferEvidence(req);
        CanonicalEvidenceType ce = new CanonicalEvidenceType();
        ce.setAny(DE4AT42Marshaller.legalEntity().getAsDocument(t42Evidence).getDocumentElement());
        res.setCanonicalEvidence(ce);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO).getAsString(res));
    }

    @PostMapping("/dr1/usi/transferevidence")
    public ResponseEntity<String> dr1usiresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.drUsiRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestTransferEvidenceUSIDRType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        var res = DE4AResponseDocumentHelper.createResponseError(true);
        res.setErrorList(new ErrorListType());
        ErrorType error = new ErrorType();
        error.setCode("asdf");
        error.setText("asdf");
        res.getErrorList().getError().add(error);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drUsiResponseMarshaller().getAsString(res));
    }

    @PostMapping("/dt1/usi/transferevidence")
    public ResponseEntity<String> dt1usiresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.dtUsiRequestMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO);
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestTransferEvidenceUSIDTType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        var res = DE4AResponseDocumentHelper.createResponseError(true);
        res.setErrorList(new ErrorListType());
        ErrorType error = new ErrorType();
        error.setCode("asdf");
        error.setText("asdf");
        res.getErrorList().getError().add(error);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.dtUsiResponseMarshaller().getAsString(res));
    }

}
