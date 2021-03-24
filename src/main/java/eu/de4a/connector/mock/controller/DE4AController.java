package eu.de4a.connector.mock.controller;

import eu.de4a.connector.mock.exampledata.CanonicalEvidenceExamples;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.connector.mock.exampledata.EvidenceID;
import eu.de4a.iem.jaxb.common.types.*;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.EDE4ACanonicalEvidenceType;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Element;

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
    public static final String DE4A_NOT_FOUND = "de4a-404";
    public static final String DE4A_BAD_REQUEST = "de4a-400";

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
        DataOwner dataOwner = DataOwner.selectDataOwner(req.getDataOwner());
        if (dataOwner == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            DE4A_NOT_FOUND,
                            String.format("no known data owners with id %s", req.getDataOwner().getIdValue())
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsString(res));
        }
        if (!dataOwner.getPilot().validDataRequestSubject(req.getDataRequestSubject())) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            DE4A_BAD_REQUEST,
                            String.format("%s for requests to %s", dataOwner.getPilot().restrictionDescription(), dataOwner.toString())
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        EvidenceID evidenceID = EvidenceID.selectEvidenceId(req.getCanonicalEvidenceId());
        if (evidenceID == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            DE4A_NOT_FOUND,
                            String.format("no known evidence id '%s'", req.getCanonicalEvidenceId())
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        String eIDASIdentifier = dataOwner.getPilot().getEIDASIdentifier(req.getDataRequestSubject());
        Element canonicalEvidence = CanonicalEvidenceExamples.getDocumentElement(dataOwner, evidenceID, eIDASIdentifier);
        if (canonicalEvidence == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            DE4A_NOT_FOUND,
                            String.format("No evidence with eIDASIdentifier '%s' found with evidenceID '%s' for %s", eIDASIdentifier, evidenceID.getId(), dataOwner.toString())));
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        CanonicalEvidenceType ce = new CanonicalEvidenceType();
        ce.setAny(canonicalEvidence);
        res.setCanonicalEvidence(ce);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
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
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(res));
    }

    @PostMapping("/de1/usi/forwardevidence")
    public ResponseEntity<String> de1usiresp(InputStream body) throws MarshallException {
        //todo: check dataowner and use CanonicalEvidenceType from Pilot enum.
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
    public ResponseEntity<String> dr1imresp(InputStream body) throws MarshallException {
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
        DataOwner dataOwner = DataOwner.selectDataOwner(req.getDataOwner());
        if (dataOwner == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            DE4A_NOT_FOUND,
                            String.format("no known data owners with id %s", req.getDataOwner().getIdValue())
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsString(res));
        }
        if (!dataOwner.getPilot().validDataRequestSubject(req.getDataRequestSubject())) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            DE4A_BAD_REQUEST,
                            String.format("%s for requests to %s", dataOwner.getPilot().restrictionDescription(), dataOwner.toString())
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        EvidenceID evidenceID = EvidenceID.selectEvidenceId(req.getCanonicalEvidenceId());
        if (evidenceID == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            DE4A_NOT_FOUND,
                            String.format("no known evidence id '%s'", req.getCanonicalEvidenceId())
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        String eIDASIdentifier = dataOwner.getPilot().getEIDASIdentifier(req.getDataRequestSubject());
        Element canonicalEvidence = CanonicalEvidenceExamples.getDocumentElement(dataOwner, evidenceID, eIDASIdentifier);
        if (canonicalEvidence == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            DE4A_NOT_FOUND,
                            String.format("No evidence with eIDASIdentifier '%s' found for %s", eIDASIdentifier, dataOwner.toString())));
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        CanonicalEvidenceType ce = new CanonicalEvidenceType();
        ce.setAny(canonicalEvidence);
        res.setCanonicalEvidence(ce);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
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
        //todo: check dataowner and use CanonicalEvidenceType from Pilot enum.
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
