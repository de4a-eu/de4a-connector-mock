package eu.de4a.connector.mock.controller;

import eu.de4a.jaxb.de1.usi.RequestForwardEvidence;
import eu.de4a.jaxb.de1.usi.ResponseForwardEvidence;
import eu.de4a.jaxb.do1.im.RequestExtractEvidence;
import eu.de4a.jaxb.do1.im.ResponseExtractEvidence;
import eu.de4a.jaxb.dr1.idk.RequestLookupEvidenceServiceData;
import eu.de4a.jaxb.dr1.idk.RequestLookupRoutingInformation;
import eu.de4a.jaxb.dr1.idk.ResponseLookupEvidenceServiceData;
import eu.de4a.jaxb.dr1.idk.ResponseLookupRoutingInformation;
import eu.de4a.jaxb.dr1.im.RequestTransferEvidence;
import eu.de4a.jaxb.dr1.im.ResponseTransferEvidence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
public class DE4AController {

    @Autowired
    ResponseExtractEvidence do1imresp;
    @Autowired
    eu.de4a.jaxb.do1.usi.ResponseExtractEvidence do1usiresp;
    @Autowired
    ResponseForwardEvidence de1usiresp;
    @Autowired
    ResponseLookupEvidenceServiceData dr1idkevidenceresp;
    @Autowired
    ResponseLookupRoutingInformation dr1idkroutingresp;
    @Autowired
    ResponseTransferEvidence dr1imresp;
    @Autowired
    eu.de4a.jaxb.dr1.usi.ResponseTransferEvidence dr1usiresp;
    @Autowired
    eu.de4a.jaxb.dt1.usi.ResponseTransferEvidence dt1usiresp;

    @PostMapping("/do1/im/extractevidence")
    public ResponseExtractEvidence DO1ImRequestExtractEvidence(@Valid @RequestBody RequestExtractEvidence extractEvidence) {
        log.info("the request: {}", ToStringBuilder.reflectionToString(extractEvidence));
        return do1imresp;
    }

    @PostMapping("/do1/usi/extractevidence")
    public eu.de4a.jaxb.do1.usi.ResponseExtractEvidence DO1USIRequestExtractEvidence(@Valid @RequestBody eu.de4a.jaxb.do1.usi.RequestExtractEvidence extractEvidence) {
        log.info("the request: {}", ToStringBuilder.reflectionToString(extractEvidence));
        return do1usiresp;
    }

    @PostMapping("/de1/usi/forwardevidence")
    public ResponseForwardEvidence de1usiresp(@Valid @RequestBody RequestForwardEvidence request) {
        return de1usiresp;
    }

    @PostMapping("/dr1/idk/lookupevidenceservicedata")
    public ResponseLookupEvidenceServiceData dr1idkevidenceresp(@Valid @RequestBody RequestLookupEvidenceServiceData request) {
        return dr1idkevidenceresp;
    }

    @PostMapping("/dr1/idk/lookuproutinginformation")
    public ResponseLookupRoutingInformation dr1idkroutingresp(@Valid @RequestBody RequestLookupRoutingInformation request) {
        return dr1idkroutingresp;
    }

    @PostMapping("/dr1/im/transferevidence")
    public ResponseTransferEvidence dr1imresp(@Valid @RequestBody RequestTransferEvidence request) {
        log.debug("dr1im: {}", ToStringBuilder.reflectionToString(dr1imresp));
        return dr1imresp;
    }

    @PostMapping("/dr1/usi/transferevidence")
    public eu.de4a.jaxb.dr1.usi.ResponseTransferEvidence dr1usiresp(@Valid @RequestBody eu.de4a.jaxb.dr1.usi.RequestTransferEvidence request) {
        return dr1usiresp;
    }

    @PostMapping("/dt1/usi/transferevidence")
    public eu.de4a.jaxb.dt1.usi.ResponseTransferEvidence dt1usiresp(@Valid @RequestBody eu.de4a.jaxb.dt1.usi.RequestTransferEvidence request) {
        return dt1usiresp;
    }
}
