package eu.de4a.connector.mock.controller;

import eu.de4a.connector.mock.config.JaxbConfig;
import eu.de4a.edm.jaxb.common.types.AckType;
import eu.de4a.edm.jaxb.common.types.ErrorListType;
import eu.de4a.edm.jaxb.common.types.ErrorType;
import eu.de4a.edm.jaxb.de_usi.RequestForwardEvidenceType;
import eu.de4a.edm.jaxb.de_usi.ResponseForwardEvidenceType;
import eu.de4a.edm.jaxb.do_im.ObjectFactory;
import eu.de4a.edm.jaxb.do_im.RequestExtractEvidenceType;
import eu.de4a.edm.jaxb.do_im.ResponseExtractEvidenceType;
import eu.de4a.edm.jaxb.dr_idk.RequestLookupEvidenceServiceDataType;
import eu.de4a.edm.jaxb.dr_idk.RequestLookupRoutingInformationType;
import eu.de4a.edm.jaxb.dr_idk.ResponseLookupEvidenceServiceDataType;
import eu.de4a.edm.jaxb.dr_idk.ResponseLookupRoutingInformationType;
import eu.de4a.edm.jaxb.dr_im.RequestTransferEvidenceType;
import eu.de4a.edm.jaxb.dr_im.ResponseTransferEvidenceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
public class DE4AController {

    @Autowired
    Jaxb2Marshaller marshaller;
    @Autowired
    JaxbConfig.Helper helper;

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
        ObjectFactory of = new ObjectFactory();
        try {
            RequestExtractEvidenceType req = helper.unmarshall(new StreamSource(body), ObjectFactory._RequestExtractEvidence_QNAME);
        } catch (MarshallException ex) {
            throw new EndpointException("could not unmarshall input", ex, (error) -> {
                ResponseExtractEvidenceType res = new ResponseExtractEvidenceType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                return helper.marshall(of.createResponseExtractEvidence(res));
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(helper.marshall(of.createResponseExtractEvidence(do1imresp)));
    }

    @PostMapping("/do1/usi/extractevidence")
    public ResponseEntity<String> DO1USIRequestExtractEvidence(InputStream body) {
        eu.de4a.edm.jaxb.do_usi.ObjectFactory of = new eu.de4a.edm.jaxb.do_usi.ObjectFactory();
        try {
            eu.de4a.edm.jaxb.do_usi.RequestExtractEvidenceType req = helper.unmarshall(new StreamSource(body), eu.de4a.edm.jaxb.do_usi.ObjectFactory._RequestExtractEvidence_QNAME);
        } catch (MarshallException ex) {
            throw new EndpointException("could not unmarshall input", ex, (error) -> {
                var res = new eu.de4a.edm.jaxb.do_usi.ResponseExtractEvidenceType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                res.setAck(AckType.KO);
                return helper.marshall(of.createResponseExtractEvidence(res));
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(helper.marshall(of.createResponseExtractEvidence(do1usiresp)));
    }

    @PostMapping("/de1/usi/forwardevidence")
    public ResponseEntity<String> de1usiresp(InputStream body) {
        eu.de4a.edm.jaxb.de_usi.ObjectFactory of = new eu.de4a.edm.jaxb.de_usi.ObjectFactory();
        try {
            RequestForwardEvidenceType req = helper.unmarshall(new StreamSource(body), eu.de4a.edm.jaxb.de_usi.ObjectFactory._RequestForwardEvidence_QNAME);
        } catch (MarshallException ex) {
            throw new EndpointException("could not unmarshall input", ex, (error) -> {
                var res = new ResponseForwardEvidenceType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                res.setAck(AckType.KO);
                return helper.marshall(of.createResponseForwardEvidence(res));
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(helper.marshall(of.createResponseForwardEvidence(de1usiresp)));
    }

    @PostMapping("/dr1/idk/lookupevidenceservicedata")
    public ResponseEntity<String> dr1idkevidenceresp(InputStream body) {
        eu.de4a.edm.jaxb.dr_idk.ObjectFactory of = new eu.de4a.edm.jaxb.dr_idk.ObjectFactory();
        try {
            RequestLookupEvidenceServiceDataType req = helper.unmarshall(new StreamSource(body), eu.de4a.edm.jaxb.dr_idk.ObjectFactory._RequestLookupEvidenceServiceData_QNAME);
        } catch (MarshallException ex) {
            throw new EndpointException("could not unmarshall input", ex, (error) -> {
                var res = new ResponseLookupEvidenceServiceDataType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                return helper.marshall(of.createResponseLookupEvidenceServiceData(res));
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(helper.marshall(of.createResponseLookupEvidenceServiceData(dr1idkevidenceresp)));
    }

    @PostMapping("/dr1/idk/lookuproutinginformation")
    public ResponseEntity<String> dr1idkroutingresp(InputStream body) {
        eu.de4a.edm.jaxb.dr_idk.ObjectFactory of = new eu.de4a.edm.jaxb.dr_idk.ObjectFactory();
        try {
            RequestLookupRoutingInformationType req = helper.unmarshall(new StreamSource(body), eu.de4a.edm.jaxb.dr_idk.ObjectFactory._RequestLookupRoutingInformation_QNAME);
        } catch (MarshallException ex) {
            throw new EndpointException("could not unmarshall input", ex, (error) -> {
                var res = new ResponseLookupRoutingInformationType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setError(error);
                return helper.marshall(of.createResponseLookupRoutingInformation(res));
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(helper.marshall(of.createResponseLookupRoutingInformation(dr1idkroutingresp)));
    }

    @PostMapping("/dr1/im/transferevidence")
    public ResponseEntity<String> dr1imresp(InputStream body) {
        eu.de4a.edm.jaxb.dr_im.ObjectFactory of = new eu.de4a.edm.jaxb.dr_im.ObjectFactory();
        try {
            RequestTransferEvidenceType req = helper.unmarshall(new StreamSource(body), eu.de4a.edm.jaxb.dr_im.ObjectFactory._RequestTransferEvidence_QNAME);
        } catch (MarshallException ex) {
            throw new EndpointException("could not unmarshall input", ex, (error) -> {
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
                return helper.marshall(of.createResponseTransferEvidence(res));
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(helper.marshall(of.createResponseTransferEvidence(dr1imresp)));
    }

    @PostMapping("/dr1/usi/transferevidence")
    public ResponseEntity<String> dr1usiresp(InputStream body) {
        eu.de4a.edm.jaxb.dr_usi.ObjectFactory of = new eu.de4a.edm.jaxb.dr_usi.ObjectFactory();
        try {
            eu.de4a.edm.jaxb.dr_usi.RequestTransferEvidenceType req = helper.unmarshall(new StreamSource(body), eu.de4a.edm.jaxb.dr_usi.ObjectFactory._RequestTransferEvidence_QNAME);
        } catch (MarshallException ex) {
            throw new EndpointException("could not unmarshall input", ex, (error) -> {
                var res = new eu.de4a.edm.jaxb.dr_usi.ResponseTransferEvidenceType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                res.setAck(AckType.KO);
                return helper.marshall(of.createResponseTransferEvidence(res));
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(helper.marshall(of.createResponseTransferEvidence(dr1usiresp)));
    }

    @PostMapping("/dt1/usi/transferevidence")
    public ResponseEntity<String> dt1usiresp(InputStream body) {
        eu.de4a.edm.jaxb.dt_usi.ObjectFactory of = new eu.de4a.edm.jaxb.dt_usi.ObjectFactory();
        try {
            eu.de4a.edm.jaxb.dt_usi.RequestTransferEvidenceType req = helper.unmarshall(new StreamSource(body), eu.de4a.edm.jaxb.dt_usi.ObjectFactory._RequestTransferEvidence_QNAME);
        } catch (MarshallException ex) {
            throw new EndpointException("could not unmarshall input", ex, (error) -> {
                var res = new eu.de4a.edm.jaxb.dt_usi.ResponseTransferEvidenceType();
                ErrorListType errorListType = new ErrorListType();
                List<ErrorType> errorList = errorListType.getError();
                errorList.add(error);
                res.setErrorList(errorListType);
                res.setAck(AckType.KO);
                return helper.marshall(of.createResponseTransferEvidence(res));
            });
        }
        return ResponseEntity.status(HttpStatus.OK).body(helper.marshall(of.createResponseTransferEvidence(dt1usiresp)));
    }


}
