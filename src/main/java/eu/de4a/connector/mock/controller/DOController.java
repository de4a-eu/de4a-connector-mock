package eu.de4a.connector.mock.controller;

import eu.de4a.connector.mock.exampledata.CanonicalEvidenceExamples;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.connector.mock.exampledata.EvidenceID;
import eu.de4a.iem.jaxb.common.types.*;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
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
@Profile("do")
public class DOController {

    @PostMapping("${do.endpoint.im}")
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
                            ErrorCodes.DE4A_NOT_FOUND.getCode(),
                            String.format("no known data owners with urn %s", req.getDataOwner().getAgentUrn())
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsString(res));
        }
        if (!dataOwner.getPilot().validDataRequestSubject(req.getDataRequestSubject())) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            ErrorCodes.DE4A_BAD_REQUEST.getCode(),
                            String.format("%s for requests to %s", dataOwner.getPilot().restrictionDescription(), dataOwner.toString())
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        EvidenceID evidenceID = EvidenceID.selectEvidenceId(req.getCanonicalEvidenceTypeId());
        if (evidenceID == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            ErrorCodes.DE4A_NOT_FOUND.getCode(),
                            String.format("no known evidence type id '%s'", req.getCanonicalEvidenceTypeId())
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
                            ErrorCodes.DE4A_NOT_FOUND.getCode(),
                            String.format("No evidence with eIDASIdentifier '%s' found with evidenceID '%s' for %s", eIDASIdentifier, evidenceID.getId(), dataOwner.toString())));
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        CanonicalEvidenceType ce = new CanonicalEvidenceType();
        ce.setAny(canonicalEvidence);
        res.setCanonicalEvidence(ce);
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
    }

    @PostMapping("${do.endpoint.usi}")
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
}
