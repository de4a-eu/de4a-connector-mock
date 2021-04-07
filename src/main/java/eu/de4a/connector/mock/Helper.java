package eu.de4a.connector.mock;

import eu.de4a.iem.jaxb.common.types.RequestExtractEvidenceIMType;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIIMDRType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class Helper {

    public static RequestExtractEvidenceIMType buildDoImRequest(RequestTransferEvidenceUSIIMDRType drRequest) {
        RequestExtractEvidenceIMType req = new RequestExtractEvidenceIMType();
        req.setRequestId(drRequest.getRequestId());
        req.setSpecificationId(drRequest.getSpecificationId());
        req.setTimeStamp(LocalDateTime.now());
        req.setProcedureId(drRequest.getProcedureId());
        req.setDataEvaluator(drRequest.getDataEvaluator());
        req.setDataOwner(drRequest.getDataOwner());
        req.setDataRequestSubject(drRequest.getDataRequestSubject());
        req.setRequestGrounds(drRequest.getRequestGrounds());
        req.setCanonicalEvidenceTypeId(drRequest.getCanonicalEvidenceTypeId());
        req.setAdditionalParameters(drRequest.getAdditionalParameters());
        return req;
    }

    public static String getStackTrace(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

}
