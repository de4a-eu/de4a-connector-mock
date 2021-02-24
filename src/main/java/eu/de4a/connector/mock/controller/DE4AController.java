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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;

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
        RequestExtractEvidenceType req = marshaller.read(body);
        if (req == null) {
            //todo: get info from exception
            throw new EndpointException("could not unmarshall input", (error) -> {
                ResponseExtractEvidenceType res = new ResponseExtractEvidenceType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                return DE4AMarshaller.doImResponseMarshaller().getAsString(res);
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doImResponseMarshaller().getAsString(do1imresp));
    }

    @PostMapping("/do1/usi/extractevidence")
    public ResponseEntity<String> DO1USIRequestExtractEvidence(InputStream body) {
        eu.de4a.edm.jaxb.do_usi.RequestExtractEvidenceType req = DE4AMarshaller.doUsiRequestMarshaller().read(body);

        if (req == null) {
            throw new EndpointException("could not unmarshall input", (error) -> {
                var res = new eu.de4a.edm.jaxb.do_usi.ResponseExtractEvidenceType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                res.setAck(AckType.KO);
                return DE4AMarshaller.doUsiResponseMarshaller().getAsString(res);
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.doUsiResponseMarshaller().getAsString(do1usiresp));
    }

    @PostMapping("/de1/usi/forwardevidence")
    public ResponseEntity<String> de1usiresp(InputStream body) {
        RequestForwardEvidenceType req = DE4AMarshaller.deUsiRequestMarshaller().read(body);
        if (req == null) {
            throw new EndpointException("could not unmarshall input", (error) -> {
                var res = new ResponseForwardEvidenceType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                res.setAck(AckType.KO);
                return DE4AMarshaller.deUsiResponseMarshaller().getAsString(res);
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.deUsiResponseMarshaller().getAsString(de1usiresp));
    }

    @PostMapping("/dr1/idk/lookupevidenceservicedata")
    public ResponseEntity<String> dr1idkevidenceresp(InputStream body) {
        RequestLookupEvidenceServiceDataType req = DE4AMarshaller.idkRequestLookupEvidenceServiceDataMarshaller().read(body);
        if (req == null) {
            throw new EndpointException("could not unmarshall input", (error) -> {
                var res = new ResponseLookupEvidenceServiceDataType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                return DE4AMarshaller.idkResponseLookupEvidenceServiceDataMarshaller().getAsString(res);
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.idkResponseLookupEvidenceServiceDataMarshaller().getAsString(dr1idkevidenceresp));
    }

    @PostMapping("/dr1/idk/lookuproutinginformation")
    public ResponseEntity<String> dr1idkroutingresp(InputStream body) {
        RequestLookupRoutingInformationType req = DE4AMarshaller.idkRequestLookupRoutingInformationMarshaller().read(body);
        if (req == null) {
            throw new EndpointException("could not unmarshall input", (error) -> {
                var res = new ResponseLookupRoutingInformationType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setError(error);
                return DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller().getAsString(res);
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.idkResponseLookupRoutingInformationMarshaller().getAsString(dr1idkroutingresp));
    }

    @PostMapping("/dr1/im/transferevidence")
    public ResponseEntity<String> dr1imresp(InputStream body) {
        RequestTransferEvidenceType req = DE4AMarshaller.drImRequestMarshaller().read(body);
        if (req == null) {
            throw new EndpointException("could not unmarshall input", (error) -> {
                var res = new ResponseTransferEvidenceType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                res.setRequestId(dr1imresp.getRequestId());
                res.setSpecificationId(dr1imresp.getSpecificationId());
                res.setTimeStamp(dr1imresp.getTimeStamp());
                res.setProcedureId(dr1imresp.getProcedureId());
                res.setDataEvaluator(dr1imresp.getDataEvaluator());
                res.setDataOwner(dr1imresp.getDataOwner());
                res.setDataRequestSubject(dr1imresp.getDataRequestSubject());
                res.setCanonicalEvidenceId(dr1imresp.getCanonicalEvidenceId());
                return DE4AMarshaller.drImResponseMarshaller().getAsString(res);
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drImResponseMarshaller().getAsString(dr1imresp));
    }

    @PostMapping("/dr1/usi/transferevidence")
    public ResponseEntity<String> dr1usiresp(InputStream body) {
        eu.de4a.edm.jaxb.dr_usi.RequestTransferEvidenceType req = DE4AMarshaller.drUsiRequestMarshaller().read(body);
        if (req == null) {
            throw new EndpointException("could not unmarshall input", (error) -> {
                var res = new eu.de4a.edm.jaxb.dr_usi.ResponseTransferEvidenceType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                res.setAck(AckType.KO);
                return DE4AMarshaller.drUsiResponseMarshaller().getAsString(res);
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.drUsiResponseMarshaller().getAsString(dr1usiresp));
    }

    @PostMapping("/dt1/usi/transferevidence")
    public ResponseEntity<String> dt1usiresp(InputStream body) {
        eu.de4a.edm.jaxb.dt_usi.RequestTransferEvidenceType req = DE4AMarshaller.dtUsiRequestMarshaller().read(body);
        if (req == null) {
            throw new EndpointException("could not unmarshall input", (error) -> {
                var res = new eu.de4a.edm.jaxb.dt_usi.ResponseTransferEvidenceType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                res.setAck(AckType.KO);
                return DE4AMarshaller.dtUsiResponseMarshaller().getAsString(res);
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(DE4AMarshaller.dtUsiResponseMarshaller().getAsString(dt1usiresp));
    }


}
