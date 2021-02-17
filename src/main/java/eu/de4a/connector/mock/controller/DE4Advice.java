package eu.de4a.connector.mock.controller;

import eu.de4a.jaxb.common.*;
import eu.de4a.jaxb.de1.usi.ResponseForwardEvidence;
import eu.de4a.jaxb.do1.im.ResponseExtractEvidence;
import eu.de4a.jaxb.dr1.idk.ResponseLookupEvidenceServiceData;
import eu.de4a.jaxb.dr1.idk.ResponseLookupRoutingInformation;
import eu.de4a.jaxb.dr1.im.ResponseTransferEvidence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.xml.sax.SAXParseException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Slf4j
@ControllerAdvice
public class DE4Advice {

    @Autowired
    ResponseTransferEvidence resdr1im;


    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<Object> handleUnmarshallingFailureException(HttpMessageNotReadableException ex, HttpServletRequest webRequest) {
        ResponseEntity<Object> res;
        ErrorListType errorListType = new ErrorListType();
        List<ErrorType> errorList = errorListType.getError();
        ErrorType error = new ErrorType();
        error.setCode("validation");
        error.setText(ex.getLocalizedMessage());
        errorList.add(error);
        switch (webRequest.getRequestURI()) {
            case "/do1/im/extractevidence":
                log.debug("do1 im");
                ResponseExtractEvidence resdo1im = new ResponseExtractEvidence();
                resdo1im.setErrorList(errorListType);
                res = ResponseEntity.badRequest().body(resdo1im);
                break;
            case "/do1/usi/extractevidence":
                eu.de4a.jaxb.do1.usi.ResponseExtractEvidence resdo1usi = new eu.de4a.jaxb.do1.usi.ResponseExtractEvidence();
                resdo1usi.setErrorList(errorListType);
                resdo1usi.setAck(AckType.KO);
                res = ResponseEntity.badRequest().body(resdo1usi);
                break;
            case "/de1/usi/forwardevidence":
                ResponseForwardEvidence resde1usi = new ResponseForwardEvidence();
                resde1usi.setErrorList(errorListType);
                resde1usi.setAck(AckType.KO);
                res = ResponseEntity.badRequest().body(resde1usi);
                break;
            case "/dr1/idk/lookupevidenceservicedata":
                ResponseLookupEvidenceServiceData resdr1idkev = new ResponseLookupEvidenceServiceData();
                resdr1idkev.setErrorList(errorListType);
                res = ResponseEntity.badRequest().body(resdr1idkev);
                break;
            case "/dr1/idk/lookuproutinginformation":
                ResponseLookupRoutingInformation resdr1idkrou = new ResponseLookupRoutingInformation();
                resdr1idkrou.setError(error);
                res = ResponseEntity.badRequest().body(resdr1idkrou);
                break;
            case "/dr1/im/transferevidence":
                resdr1im.setErrorList(errorListType);
                resdr1im.setCanonicalEvidence(null);
                resdr1im.setDomesticEvidenceList(null);
                res = ResponseEntity.badRequest().body(resdr1im);
                break;
            case "/dr1/usi/transferevidence":
                eu.de4a.jaxb.dr1.usi.ResponseTransferEvidence resdr1usi = new eu.de4a.jaxb.dr1.usi.ResponseTransferEvidence();
                resdr1usi.setErrorList(errorListType);
                resdr1usi.setAck(AckType.KO);
                res = ResponseEntity.badRequest().body(resdr1usi);
                break;
            case "/dt1/usi/transferevidence":
                eu.de4a.jaxb.dt1.usi.ResponseTransferEvidence resdt1usi = new eu.de4a.jaxb.dt1.usi.ResponseTransferEvidence();
                resdt1usi.setErrorList(errorListType);
                resdt1usi.setAck(AckType.KO);
                res = ResponseEntity.badRequest().body(resdt1usi);
                break;
            default:
                res = ResponseEntity.badRequest().body("");
        }

        return res;
    }
}
