package eu.de4a.connector.mock.controller;

import eu.de4a.connector.mock.CanonicalEvidenceExamples;
import eu.de4a.iem.jaxb.common.idtypes.LegalEntityIdentifierType;
import eu.de4a.iem.jaxb.common.types.*;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.EDE4ACanonicalEvidenceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
public class DE4AController {

    @Autowired
    EvidenceServiceType evidenceServiceType;
    @Autowired
    IssuingAuthorityType issuingAuthorityType;

    //todo: use a correct error code
    public static final String NO_SUCH_EVIDENCE = "de4a-404";
    public static final String WRONG_DATA_SUBJECT_ID_TYPE = "de4a-400";

    private Element getCanonicalEvidence(String eIDASLegalEntityIdentifier) throws IOException{
        if (CanonicalEvidenceExamples.T42_SE.isEIDASLegalIdentifier(eIDASLegalEntityIdentifier)) {
            return CanonicalEvidenceExamples.T42_SE.getDocumentElement();
        } else if (CanonicalEvidenceExamples.T42_NL.isEIDASLegalIdentifier(eIDASLegalEntityIdentifier)) {
            return CanonicalEvidenceExamples.T42_NL.getDocumentElement();
        } else if (CanonicalEvidenceExamples.T42_RO.isEIDASLegalIdentifier(eIDASLegalEntityIdentifier)) {
            return CanonicalEvidenceExamples.T42_RO.getDocumentElement();
        } else {
            return null;
        }
    }

    @PostMapping("/do1/im/extractevidence")
    public ResponseEntity<String> DO1ImRequestExtractEvidence(InputStream body) throws IOException {
        var marshaller = DE4AMarshaller.doImRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> MarshallErrorHandler.getInstance().postError(errorKey, ex));
        RequestExtractEvidenceIMType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        var res = DE4AResponseDocumentHelper.createResponseExtractEvidence(req);
        LegalEntityIdentifierType legalId = req.getDataRequestSubject().getDataSubjectCompany();
        if (legalId == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            WRONG_DATA_SUBJECT_ID_TYPE,
                            "DataRequestSubject must be 'DataSubjectCompany'"
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V04).getAsString(res));
        }
        String eIDASLegalIdentifier = legalId.getLegalEntityIdentifier();
        Element canonicalEvidence = getCanonicalEvidence(eIDASLegalIdentifier);
        if (canonicalEvidence == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            NO_SUCH_EVIDENCE,
                            String.format("No evidence with LegalEntityIdentifier '%s' found", eIDASLegalIdentifier)));
            res.setErrorList(errorListType);
        } else {
            CanonicalEvidenceType ce = new CanonicalEvidenceType();
            ce.setAny(canonicalEvidence);
            res.setCanonicalEvidence(ce);
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V04).getAsString(res));
    }

    @PostMapping("/do1/usi/extractevidence")
    public ResponseEntity<String> DO1USIRequestExtractEvidence(InputStream body) throws MarshallException, IOException {
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
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(res));
    }

    @PostMapping("/de1/usi/forwardevidence")
    public ResponseEntity<String> de1usiresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.deUsiRequestMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V04);
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestForwardEvidenceType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        ResponseErrorType res = DE4AResponseDocumentHelper.createResponseError(true);
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
    public ResponseEntity<String> dr1imresp(InputStream body) throws MarshallException, IOException {
        var marshaller = DE4AMarshaller.drImRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestTransferEvidenceUSIIMDRType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        var res = DE4AResponseDocumentHelper.createResponseTransferEvidence(req);
        LegalEntityIdentifierType legalId = req.getDataRequestSubject().getDataSubjectCompany();
        if (legalId == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            WRONG_DATA_SUBJECT_ID_TYPE,
                            "DataRequestSubject must be 'DataSubjectCompany'"
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V04).getAsString(res));
        }
        String eIDASLegalIdentifier = legalId.getLegalEntityIdentifier();
        Element canonicalEvidence = getCanonicalEvidence(eIDASLegalIdentifier);
        if (canonicalEvidence == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            NO_SUCH_EVIDENCE,
                            String.format("No evidence with LegalEntityIdentifier '%s' found", eIDASLegalIdentifier)));
            res.setErrorList(errorListType);
        } else {
            CanonicalEvidenceType ce = new CanonicalEvidenceType();
            ce.setAny(canonicalEvidence);
            res.setCanonicalEvidence(ce);
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V04).getAsString(res));
    }

    @PostMapping("/dr1/usi/transferevidence")
    public ResponseEntity<String> dr1usiresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.drUsiRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestTransferEvidenceUSIIMDRType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        var res = DE4AResponseDocumentHelper.createResponseError(true);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drUsiResponseMarshaller().getAsString(res));
    }

    @PostMapping("/dt1/usi/transferevidence")
    public ResponseEntity<String> dt1usiresp(InputStream body) throws MarshallException {
        var marshaller = DE4AMarshaller.dtUsiRequestMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO_V04);
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestTransferEvidenceUSIDTType req = marshaller.read(body);
        if (req == null) {
            throw new MarshallException(errorKey);
        }
        var res = DE4AResponseDocumentHelper.createResponseError(true);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.dtUsiResponseMarshaller().getAsString(res));
    }

}
