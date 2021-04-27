package eu.de4a.connector.mock.controller;

import eu.de4a.connector.mock.config.DOConfig;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.connector.mock.preview.PreviewStorage;
import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Controller
@Profile("do")
public class DOPreviewController {
    @Autowired
    PreviewStorage previewStorage;

    @Autowired
    DOConfig doConfig;

    @RequestMapping(value = "${mock.do.preview.endpoint}")
    public String doIndex(Model model) {
        model.addAttribute("doConfig", doConfig);
        return "doIndex";
    }

    @ExceptionHandler({InterruptedException.class, ExecutionException.class, TimeoutException.class})
    public ResponseEntity<Object> timeoutRequest(Exception ex) {
        log.error("Request for evidence failed: {}", ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "${mock.do.preview.endpoint}/${mock.do.preview.evidence.get.endpoint}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getEvidence(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
        RequestTransferEvidenceUSIDTType request;
        log.debug("doConfig timeout: {}", doConfig.getPreviewEvidenceTimeout());
        request = previewStorage.getRequest(requestId).get();
        DataOwner dataOwner = DataOwner.selectDataOwner(request.getDataOwner());
        return ResponseEntity
                .status(200)
                .body(DE4AMarshaller.dtUsiRequestMarshaller(
                        dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(request));
    }

    @GetMapping(value = "${mock.do.preview.endpoint}/${mock.do.preview.evidence.accept.endpoint}")
    public ResponseEntity<Object> acceptEvidence(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
        RequestTransferEvidenceUSIDTType request;
        request = previewStorage.getRequest(requestId).get();
        try {
            Boolean success = DOController.sendDTRequest(request, log::error).get();
            if (!success) {
                ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Error sending message");
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.debug("request inteupted: {}", ex.getMessage());
            ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("request interupted");
        }
        previewStorage.removePreview(requestId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "${mock.do.preview.endpoint}/${mock.do.preview.evidence.reject.endpoint}")
    public ResponseEntity<Object> rejectEvidence(@PathVariable String requestId) {
        previewStorage.removePreview(requestId);
        return ResponseEntity.ok().build();
    }

}
