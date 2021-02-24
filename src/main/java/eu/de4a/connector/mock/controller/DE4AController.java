package eu.de4a.connector.mock.controller;

import eu.de4a.edm.jaxb.common.types.AckType;
import eu.de4a.edm.jaxb.common.types.ErrorListType;
import eu.de4a.edm.jaxb.common.types.ErrorType;
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

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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




    private ErrorListType buildErrorList(JAXBException exception) {
        ErrorType error = new ErrorType();
        error.setCode("marshall");
        StringWriter sw = new StringWriter();
        sw.append(exception.getCause().getLocalizedMessage());
        sw.append("\n");
        exception.printStackTrace(new PrintWriter(sw));
        //todo: The xml errors are blocked at 4000 characters
        error.setText(sw.toString().substring(0, 4000));
        ErrorListType errorListType = new ErrorListType();
        errorListType.addError(error);
        return  errorListType;
    }

    @PostMapping("/do1/im/extractevidence")
    public ResponseEntity<String> DO1ImRequestExtractEvidence(InputStream body) throws InterruptedException, ExecutionException, TimeoutException {
        var marshaller = DE4AMarshaller.doImRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestExtractEvidenceType req = marshaller.read(body);
        if (req == null) {
            // TODO [ph] I suggest to return a badRequest only with the error message but not XML payload, because that cannot be filled in real-life
            JAXBException exception = MarshallErrorHandler.getInstance().getError(errorKey).get(1000, TimeUnit.MILLISECONDS);
            ResponseExtractEvidenceType res = new ResponseExtractEvidenceType();
            res.setErrorList(buildErrorList(exception));
            return ResponseEntity.badRequest().body(DE4AMarshaller.doImResponseMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO).getAsString(res));
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO).getAsString(do1imresp));
    }

    @PostMapping("/do1/usi/extractevidence")
    public ResponseEntity<String> DO1USIRequestExtractEvidence(InputStream body) throws InterruptedException, ExecutionException, TimeoutException {
        var marshaller = DE4AMarshaller.doUsiRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        eu.de4a.edm.jaxb.do_usi.RequestExtractEvidenceType req = marshaller.read(body);
        if (req == null) {
            // TODO [ph] I suggest to return a badRequest only with the error message but not XML payload, because that cannot be filled in real-life
            JAXBException exception = MarshallErrorHandler.getInstance().getError(errorKey).get(1000, TimeUnit.MILLISECONDS);
            var res = new eu.de4a.edm.jaxb.do_usi.ResponseExtractEvidenceType();
            res.setErrorList(buildErrorList(exception));
            res.setAck(AckType.KO);
            return ResponseEntity.badRequest().body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(res));
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(do1usiresp));
    }

    @PostMapping("/de1/usi/forwardevidence")
    public ResponseEntity<String> de1usiresp(InputStream body) throws InterruptedException, ExecutionException, TimeoutException {
        var marshaller = DE4AMarshaller.deUsiRequestMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO);
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestForwardEvidenceType req = marshaller.read(body);
        if (req == null) {
            // TODO [ph] I suggest to return a badRequest only with the error message but not XML payload, because that cannot be filled in real-life
            JAXBException exception = MarshallErrorHandler.getInstance().getError(errorKey).get(1000, TimeUnit.MILLISECONDS);
            var res = new ResponseForwardEvidenceType();
            res.setErrorList(buildErrorList(exception));
            res.setAck(AckType.KO);
            return ResponseEntity.badRequest().body(DE4AMarshaller.deUsiResponseMarshaller().getAsString(res));
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.deUsiResponseMarshaller().getAsString(de1usiresp));
    }

    @PostMapping("/dr1/idk/lookupevidenceservicedata")
    public ResponseEntity<String> dr1idkevidenceresp(InputStream body) throws InterruptedException, ExecutionException, TimeoutException {
        var marshaller = DE4AMarshaller.idkRequestLookupEvidenceServiceDataMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestLookupEvidenceServiceDataType req = marshaller.read(body);
        if (req == null) {
            // TODO [ph] I suggest to return a badRequest only with the error message but not XML payload, because that cannot be filled in real-life
            JAXBException exception = MarshallErrorHandler.getInstance().getError(errorKey).get(1000, TimeUnit.MILLISECONDS);
            var res = new ResponseLookupEvidenceServiceDataType();
            res.setErrorList(buildErrorList(exception));
            return ResponseEntity.badRequest().body(DE4AMarshaller.idkResponseLookupEvidenceServiceDataMarshaller().getAsString(res));
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.idkResponseLookupEvidenceServiceDataMarshaller().getAsString(dr1idkevidenceresp));
    }

    @PostMapping("/dr1/idk/lookuproutinginformation")
    public ResponseEntity<String> dr1idkroutingresp(InputStream body) throws InterruptedException, ExecutionException, TimeoutException {
        var marshaller = DE4AMarshaller.idkRequestLookupRoutingInformationMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestLookupRoutingInformationType req = marshaller.read(body);
        if (req == null) {
            // TODO [ph] I suggest to return a badRequest only with the error message but not XML payload, because that cannot be filled in real-life
            JAXBException exception = MarshallErrorHandler.getInstance().getError(errorKey).get(1000, TimeUnit.MILLISECONDS);
            var res = new ResponseLookupRoutingInformationType();
            res.setError(buildErrorList(exception).getError().stream().findFirst().get());
            return ResponseEntity.badRequest().body(DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller().getAsString(res));
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller().getAsString(dr1idkroutingresp));
    }

    @PostMapping("/dr1/im/transferevidence")
    public ResponseEntity<String> dr1imresp(InputStream body) throws InterruptedException, ExecutionException, TimeoutException {
        var marshaller = DE4AMarshaller.drImRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        RequestTransferEvidenceType req = marshaller.read(body);
        if (req == null) {
            // TODO [ph] I suggest to return a badRequest only with the error message but not XML payload, because that cannot be filled in real-life
            JAXBException exception = MarshallErrorHandler.getInstance().getError(errorKey).get(1000, TimeUnit.MILLISECONDS);
            var res = new ResponseTransferEvidenceType();
            res.setErrorList(buildErrorList(exception));
            res.setRequestId(dr1imresp.getRequestId());
            res.setSpecificationId(dr1imresp.getSpecificationId());
            res.setTimeStamp(dr1imresp.getTimeStamp());
            res.setProcedureId(dr1imresp.getProcedureId());
            res.setDataEvaluator(dr1imresp.getDataEvaluator());
            res.setDataOwner(dr1imresp.getDataOwner());
            res.setDataRequestSubject(dr1imresp.getDataRequestSubject());
            res.setCanonicalEvidenceId(dr1imresp.getCanonicalEvidenceId());
            return ResponseEntity.badRequest().body(DE4AMarshaller.drImResponseMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO).getAsString(res));
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO).getAsString(dr1imresp));
    }

    @PostMapping("/dr1/usi/transferevidence")
    public ResponseEntity<String> dr1usiresp(InputStream body) throws InterruptedException, ExecutionException, TimeoutException {
        var marshaller = DE4AMarshaller.drUsiRequestMarshaller();
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        eu.de4a.edm.jaxb.dr_usi.RequestTransferEvidenceType req = marshaller.read(body);
        if (req == null) {
            // TODO [ph] I suggest to return a badRequest only with the error message but not XML payload, because that cannot be filled in real-life
            JAXBException exception = MarshallErrorHandler.getInstance().getError(errorKey).get(1000, TimeUnit.MILLISECONDS);
            var res = new eu.de4a.edm.jaxb.dr_usi.ResponseTransferEvidenceType();
            res.setErrorList(buildErrorList(exception));
            res.setAck(AckType.KO);
            return ResponseEntity.badRequest().body(DE4AMarshaller.drUsiResponseMarshaller().getAsString(res));
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drUsiResponseMarshaller().getAsString(dr1usiresp));
    }

    @PostMapping("/dt1/usi/transferevidence")
    public ResponseEntity<String> dt1usiresp(InputStream body) throws InterruptedException, ExecutionException, TimeoutException {
        var marshaller = DE4AMarshaller.dtUsiRequestMarshaller(EDE4ACanonicalEvidenceType.T42_COMPANY_INFO);
        UUID errorKey = UUID.randomUUID();
        marshaller.readExceptionCallbacks().set((ex) -> {
            MarshallErrorHandler.getInstance().postError(errorKey, ex);
        });
        eu.de4a.edm.jaxb.dt_usi.RequestTransferEvidenceType req = marshaller.read(body);
        if (req == null) {
            // TODO [ph] I suggest to return a badRequest only with the error message but not XML payload, because that cannot be filled in real-life
            JAXBException exception = MarshallErrorHandler.getInstance().getError(errorKey).get(1000, TimeUnit.MILLISECONDS);
            var res = new eu.de4a.edm.jaxb.dt_usi.ResponseTransferEvidenceType();
            res.setErrorList(buildErrorList(exception));
            res.setAck(AckType.KO);
            return ResponseEntity.badRequest().body(DE4AMarshaller.dtUsiResponseMarshaller().getAsString(res));
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.dtUsiResponseMarshaller().getAsString(dt1usiresp));
    }

}
