package eu.de4a.connector.mock.controller;

import com.helger.commons.error.level.EErrorLevel;
import eu.de4a.connector.mock.Helper;
import eu.de4a.connector.mock.exampledata.CanonicalEvidenceExamples;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.connector.mock.exampledata.EvidenceID;
import eu.de4a.iem.jaxb.common.types.CanonicalEvidenceType;
import eu.de4a.iem.jaxb.common.types.ErrorListType;
import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceIMType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import eu.de4a.kafkaclient.DE4AKafkaClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
@RequestMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
@Profile("dr")
public class DRController {

    @Value("${mock.dr.forward.enable:false}")
    private boolean forwardIM;
    @Value("${mock.dr.forward.do.im:set-some-url}")
    private String forwardIMUrl;

    @PostMapping("${mock.dr.endpoint.im}")
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
        // The data owner is needed to identify what CanonicalEvidence Schema is used, both when the request is sent to the do and when the response is just mocked in the dr.
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
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsString(res));
        }

        DE4AKafkaClient.send(EErrorLevel.INFO, String.format("Received RequestTransferEvidence, requestId: %s", req.getRequestId()));

        // if set to forward, sends a request to the do for getting the CanonicalEvidence
        if (forwardIM) {
            RequestExtractEvidenceIMType doRequest = Helper.buildDoImRequest(req);
            HttpResponse doResponse;
            String doRespBody;
            try {
                doResponse = Request.Post(forwardIMUrl)
                    .bodyStream(DE4AMarshaller.doImRequestMarshaller().getAsInputStream(doRequest), ContentType.APPLICATION_XML)
                    .execute().returnResponse();

                doRespBody = IOUtils.toString(doResponse.getEntity().getContent(), StandardCharsets.UTF_8);
                if (doResponse.getStatusLine().getStatusCode() != 200) {
                    ErrorListType errorListType = new ErrorListType();
                    errorListType.addError(
                            DE4AResponseDocumentHelper.createError(
                                    ErrorCodes.DE4A_ERROR.getCode(),
                                    String.format("error sending request to do (at %s): %s \n%s", forwardIMUrl, doResponse.getStatusLine().toString(), doRespBody)
                                            .substring(0,4000)
                            )
                    );
                    res.setErrorList(errorListType);
                    return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsString(res));
                }

            } catch (IOException ex) {
                log.error("dr forward to do exception: {}", ex.getLocalizedMessage());
                ErrorListType errorListType = new ErrorListType();
                errorListType.addError(
                        DE4AResponseDocumentHelper.createError(
                                ErrorCodes.DE4A_ERROR.getCode(),
                                String.format("error sending request to do: %s \n%s", ex.getLocalizedMessage(), Helper.getStackTrace(ex)).substring(0,4000)
                        )
                );
                res.setErrorList(errorListType);
                return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsString(res));
            }

            var doMarshaller = DE4AMarshaller.doImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType());
            UUID doErrorKey = UUID.randomUUID();
            doMarshaller.readExceptionCallbacks().set((ex) -> {
                MarshallErrorHandler.getInstance().postError(errorKey, ex);
            });
            var doImResp = doMarshaller.read(doRespBody);
            if (doImResp == null) {
                String errorMessage;
                try {
                    JAXBException doExp = MarshallErrorHandler.getInstance().getError(doErrorKey).get(1000, TimeUnit.MILLISECONDS);
                    errorMessage = String.format(": %s\n%s", doExp.getLocalizedMessage(), Helper.getStackTrace(doExp));
                } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                    errorMessage = "";
                }
                ErrorListType errorListType = new ErrorListType();
                errorListType.addError(
                        DE4AResponseDocumentHelper.createError(
                                ErrorCodes.DE4A_ERROR.getCode(),
                                String.format("could not unmarshall response from do%s", errorMessage)
                                        .substring(0,4000)
                        )
                );
                res.setErrorList(errorListType);
                return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(IDE4ACanonicalEvidenceType.NONE).getAsString(res));
            }

            res.setErrorList(doImResp.getErrorList());
            res.setCanonicalEvidence(doImResp.getCanonicalEvidence());

            DE4AKafkaClient.send(EErrorLevel.INFO, String.format("Responding to RequestTransferEvidence, requestId: %s", req.getRequestId()));

            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }

        // if not set to forward, create the mock response.
        if (!dataOwner.getPilot().validDataRequestSubject(req.getDataRequestSubject())) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            ErrorCodes.DE4A_BAD_REQUEST.getCode(),
                            String.format("%s for requests to %s", dataOwner.getPilot().restrictionDescription(), dataOwner.toString())
                    )
            );
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
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
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        String eIDASIdentifier = dataOwner.getPilot().getEIDASIdentifier(req.getDataRequestSubject());
        Element canonicalEvidence = CanonicalEvidenceExamples.getDocumentElement(dataOwner, evidenceID, eIDASIdentifier);
        if (canonicalEvidence == null) {
            ErrorListType errorListType = new ErrorListType();
            errorListType.addError(
                    DE4AResponseDocumentHelper.createError(
                            ErrorCodes.DE4A_NOT_FOUND.getCode(),
                            String.format("No evidence with eIDASIdentifier '%s' found for %s", eIDASIdentifier, dataOwner.toString())));
            res.setErrorList(errorListType);
            return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
        }
        CanonicalEvidenceType ce = new CanonicalEvidenceType();
        ce.setAny(canonicalEvidence);
        res.setCanonicalEvidence(ce);

        DE4AKafkaClient.send(EErrorLevel.INFO, String.format("Responding to RequestTransferEvidence, requestId: %s", req.getRequestId()));

        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(res));
    }

}
