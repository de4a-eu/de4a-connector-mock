package eu.de4a.connector.mock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.ClassPathResource;
import eu.de4a.connector.mock.preview.PreviewMessage;
import eu.de4a.connector.mock.config.DOConfig;
import eu.de4a.connector.mock.exampledata.DataOwner;
import eu.de4a.connector.mock.preview.PreviewStorage;
import eu.de4a.iem.jaxb.common.types.*;
import eu.de4a.iem.xml.de4a.CDE4AJAXB;
import eu.de4a.iem.xml.de4a.DE4AMarshaller;
import eu.de4a.iem.xml.de4a.DE4AResponseDocumentHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Slf4j
@Controller
@Profile("do")
public class DOPreviewController {

    @Autowired
    PreviewStorage previewStorage;

    @Autowired
    DOConfig doConfig;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SimpMessagingTemplate websocketMessaging;

    @Value("${mock.baseurl}")
    String baseUrl;


    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.endpoint.index}")
    public String doIndex(Model model) {
        model.addAttribute("doConfig", doConfig);
        return "doIndex";
    }

    @CrossOrigin(originPatterns = "*")
    @ResponseStatus(HttpStatus.SEE_OTHER)
    @PostMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.endpoint.index}")
    public ModelAndView redirectPost(InputStream bodyStream) {
        DE4AMarshaller<RequestUserRedirectionType> marshaller = DE4AMarshaller.deUsiRedirectRequestMarshaller();
        RequestUserRedirectionType redirection = marshaller.read(bodyStream);
        previewStorage.setRedirectUrl(redirection.getRequestId(), redirection.getRedirectURL());
        return new ModelAndView(
                String.format("redirect:%s%s%s?requestId=%s",
                        baseUrl,
                        doConfig.getPreviewBaseEndpoint(),
                        doConfig.getIndexEndpoint(),
                        redirection.getRequestId()));
    }

    @ExceptionHandler({InterruptedException.class, ExecutionException.class, TimeoutException.class})
    public ResponseEntity<Object> timeoutRequest(Exception ex) {
        log.error("Request for evidence failed: {}", ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.evidence.get.endpoint}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getEvidence(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
        RequestTransferEvidenceUSIDTType request;
        request = previewStorage.getRequest(requestId).get();
        DataOwner dataOwner = DataOwner.selectDataOwner(request.getDataOwner());
        return ResponseEntity
                .status(200)
                .body(DE4AMarshaller.dtUsiRequestMarshaller(
                        dataOwner.getPilot().getCanonicalEvidenceType()).getAsString(request));
    }

    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.evidence.accept.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> acceptEvidence(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
        RequestTransferEvidenceUSIDTType request;
        request = previewStorage.getRequest(requestId).get();

        ResponseUserRedirectionType redirectionType = new ResponseUserRedirectionType();
        redirectionType.setRequestId(requestId);
        redirectionType.setEvidenceStatus(EvidenceStatusType.AGREE);
        String redirectUrl = previewStorage.getRedirectUrl(requestId);
        CompletableFuture<String> locationUrl;
        if (redirectUrl.isEmpty()) {
            log.error("no redirect url recieved");
            locationUrl = CompletableFuture.completedFuture("");
        } else {
            log.debug("send de post");
            locationUrl = sendDeRedirect(redirectUrl, redirectionType, log::error );
        }
        try {
            Boolean success = DOController.sendDTRequest(doConfig.getPreviewDTUrl(), request, log::error).get();
            if (!success) {
                return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Error sending message");
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.debug("request inteupted: {}", ex.getMessage());
            return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("request interupted");
        }
        previewStorage.removePreview(requestId);

        String message;
        try {
            message = objectMapper.writeValueAsString(new PreviewMessage(PreviewMessage.Action.RM, requestId));
        } catch (JsonProcessingException ex) {
            message = "{}";
            log.error("json error");
        }
        String endpoint = String.format("%s%s", doConfig.getPreviewBaseEndpoint(), doConfig.getWebsocketMessagesEndpoint());
        log.debug("sending websocket message {}: {}", endpoint, message);
        websocketMessaging.convertAndSend(endpoint, message);

        return ResponseEntity.ok().body(locationUrl.get());
    }

    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.evidence.reject.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> rejectEvidence(@PathVariable String requestId) throws InterruptedException, TimeoutException, ExecutionException {
        RequestTransferEvidenceUSIDTType request;
        request = previewStorage.getRequest(requestId).get();
        request.setCanonicalEvidence(null);
        request.setDomesticEvidenceList(null);
        ErrorListType el = new ErrorListType();
        el.addError(DE4AResponseDocumentHelper.createError(ErrorCodes.PREVIEW_REJECTED_ERROR.getCode(), "The user rejected the evidence"));
        request.setErrorList(el);

        ResponseUserRedirectionType redirectionType = new ResponseUserRedirectionType();
        redirectionType.setRequestId(requestId);
        redirectionType.setEvidenceStatus(EvidenceStatusType.DISAGREE);
        String redirectUrl = previewStorage.getRedirectUrl(requestId);
        CompletableFuture<String> locationUrl;
        if (redirectUrl.isEmpty()) {
            log.error("no redirect url recieved");
            locationUrl = CompletableFuture.completedFuture("");
        } else {
            locationUrl = sendDeRedirect(redirectUrl, redirectionType, log::error);
        }
        try {
            Boolean success = DOController.sendDTRequest(doConfig.getPreviewDTUrl(), request, log::error).get();
            if (!success) {
                return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("Error sending message");
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.debug("request inteupted: {}", ex.getMessage());
            return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN).body("request interupted");
        }
        previewStorage.removePreview(requestId);

        String message;
        try {
            message = objectMapper.writeValueAsString(new PreviewMessage(PreviewMessage.Action.RM, requestId));
        } catch (JsonProcessingException ex) {
            message = "{}";
            log.error("json error");
        }
        String endpoint = String.format("%s%s", doConfig.getPreviewBaseEndpoint(), doConfig.getWebsocketMessagesEndpoint());
        log.debug("sending websocket message {}: {}", endpoint, message);
        websocketMessaging.convertAndSend(endpoint, message);

        return ResponseEntity.ok().body(locationUrl.get());
    }

    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.evidence.error.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> redirectError(@PathVariable String requestId) throws ExecutionException, InterruptedException {
        ResponseUserRedirectionType redirectionType = new ResponseUserRedirectionType();
        redirectionType.setRequestId(requestId);
        redirectionType.setEvidenceStatus(EvidenceStatusType.ERROR);
        String redirectUrl = previewStorage.getRedirectUrl(requestId);
        CompletableFuture<String> locationUrl;
        if (redirectUrl.isEmpty()) {
            log.error("no redirect url recieved");
            locationUrl = CompletableFuture.completedFuture("");
        } else {
            locationUrl = sendDeRedirect(redirectUrl, redirectionType, log::error);
        }
        return ResponseEntity.ok(locationUrl.get());
    }

    @GetMapping(value = "${mock.do.preview.endpoint.base}${mock.do.preview.evidence.requestId.all.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllRequestIds() {
        return ResponseEntity.ok(previewStorage.getAllRequestIds());
    }

    public static CompletableFuture<String> sendDeRedirect(
            String recipient,
            ResponseUserRedirectionType request,
            Consumer<String> onFailure) {
        HttpResponse deResp;
        try {
            deResp = Request.Post(recipient)
                    .bodyStream(DE4AMarshaller.deUsiRedirectResponseMarshaller()
                            .getAsInputStream(request), ContentType.APPLICATION_XML)
                    .execute().returnResponse();
        } catch (IOException ex) {
            onFailure.accept(String.format("Failed to send redirect post to de: %s", ex.getMessage()));
            return CompletableFuture.completedFuture("");
        }
        if (deResp.getStatusLine().getStatusCode() > 300 && deResp.getStatusLine().getStatusCode() <= 303 || deResp.getStatusLine().getStatusCode() == 307) {
            String errorString = String.format("Request sent to de got status code %s, unable to redirect to de", deResp.getStatusLine().getStatusCode());
            onFailure.accept(errorString);
            return CompletableFuture.completedFuture("");
        }
        String url = deResp.getFirstHeader("Location").toString();
        log.debug("Successfully sent de redirect post for request with id: {}, got redirect location: {}", request.getRequestId(), url);

        return CompletableFuture.completedFuture(url);
    }
}
