package eu.de4a.connector.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import eu.de4a.iem.jaxb.common.types.*;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

public class Helper {
    public static final int ERROR_TEXT_MAX_LENGTH = 4000;

    public static RequestExtractEvidenceType buildDoImRequest(RequestExtractEvidenceType drRequest) {
        return drRequest.clone ();
    }

    public static RequestExtractEvidenceType buildDoUsiRequest(RequestExtractEvidenceType drRequest) {
        return drRequest.clone ();
    }

    public static RequestTransferEvidenceUSIDTType buildDtUsiRequest(RequestExtractEvidenceType doRequest, CanonicalEvidenceType canonicalEvidence, DomesticsEvidencesType domesticEvidences, ErrorListType errorListType) {
        RequestTransferEvidenceUSIDTType req = new RequestTransferEvidenceUSIDTType();
        req.setRequestId(doRequest.getRequestId());
        req.setSpecificationId(doRequest.getSpecificationId());
        req.setTimeStamp(LocalDateTime.now());
        req.setProcedureId(doRequest.getProcedureId());
        req.setDataEvaluator(doRequest.getDataEvaluator());
        req.setDataOwner(doRequest.getDataOwner());
        req.setDataRequestSubject(doRequest.getDataRequestSubject());
        req.setCanonicalEvidence(canonicalEvidence);
        req.setDomesticEvidenceList(domesticEvidences);
        req.setErrorList(errorListType);
        return req;
    }

    public static RequestForwardEvidenceType buildDeUriRequest(RequestTransferEvidenceUSIDTType dtRequest) {
        RequestForwardEvidenceType req = new RequestForwardEvidenceType();
        req.setRequestId(dtRequest.getRequestId());
        req.setTimeStamp(LocalDateTime.now());
        req.setCanonicalEvidence(dtRequest.getCanonicalEvidence());
        req.setDomesticEvidenceList(dtRequest.getDomesticEvidenceList());
        req.setErrorList(dtRequest.getErrorList());
        return req;
    }

    public static String getStackTrace(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    public static CompletableFuture<Boolean> sendRequest(String recipient, InputStream bodyStream, Consumer<String> onFailure) {
        HttpResponse dtResp;
        try {
            dtResp = Request.Post(recipient)
                    .bodyStream(bodyStream, ContentType.APPLICATION_XML)
                    .execute().returnResponse();
        } catch (IOException ex) {
            onFailure.accept(String.format("Failed to send request to dt: %s", ex.getMessage()));
            return CompletableFuture.completedFuture(false);
        }
        if (dtResp.getStatusLine().getStatusCode() != 200) {
            onFailure.accept(String.format("Request sent to dt (%s) got status code %s",
                    recipient,
                    dtResp.getStatusLine().getStatusCode()));
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.completedFuture(true);
    }

    public static ErrorType doConnectionError(String service, String explanation) {
        return DE4AResponseDocumentHelper.createError(
                "10503",
                String
                        .format("Connection error with %s - %s", service, explanation)
                        .substring(0, ERROR_TEXT_MAX_LENGTH)
        );
    }

    public static ErrorType doServiceNotFound(String service) {
        return DE4AResponseDocumentHelper.createError(
                "10501",
                String
                        .format("Service requested %s not found", service)
                        .substring(0, ERROR_TEXT_MAX_LENGTH)
        );
    }

    public static ErrorType doAccessingData(String service, String explanation) {
        return DE4AResponseDocumentHelper.createError(
                "10506",
                String
                        .format("Connection error with %s - %s", service, explanation)
                        .substring(0, ERROR_TEXT_MAX_LENGTH)
        );
    }

    public static ErrorType doErrorOnResponse(String service, String explanation) {
        return DE4AResponseDocumentHelper.createError(
                "10504",
                String
                        .format("Error on response from %s - %s", service, explanation)
                        .substring(0, ERROR_TEXT_MAX_LENGTH)
        );
    }

    public static ErrorType doGenericError(String explanation) {
        return DE4AResponseDocumentHelper.createError(
                "10507",
                explanation.substring(0, ERROR_TEXT_MAX_LENGTH)
        );
    }

    public static ErrorType doErrorExtractingEvidence() {
        return DE4AResponseDocumentHelper.createError(
                "40510",
                "Error extracting evidence"
        );
    }

    public static ErrorType doEvidenceNotAvailable() {
        return DE4AResponseDocumentHelper.createError(
                "40515",
                "Evidence not available yet (delayed)"
        );
    }

    public static ErrorType doIdentityMatchingError() {
        return DE4AResponseDocumentHelper.createError(
                "40511",
                "Error in identity matching"
        );
    }

    public static ErrorType doUnsuccessfulPreview() {
        return DE4AResponseDocumentHelper.createError(
                "40512",
                "Unsuccessful completion of preview"
        );
    }

    public static ErrorType doFailedReestablishUser() {
        return DE4AResponseDocumentHelper.createError(
                "40514",
                "Failed to re-establish user identity"
        );
    }

    public static ErrorType doRejectedPreview() {
        return DE4AResponseDocumentHelper.createError(
                "40513",
                "Preview rejected by user"
        );
    }

    public static ErrorType doEvidenceError() {
        return DE4AResponseDocumentHelper.createError(
                "40516",
                "evidence"
        );
    }

}
