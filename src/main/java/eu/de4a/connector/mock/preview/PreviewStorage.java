package eu.de4a.connector.mock.preview;

import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import eu.de4a.iem.xml.de4a.IDE4ACanonicalEvidenceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@Profile("do")
public class PreviewStorage {

    // For concurrency handling, I'm assuming the requestId is unique. Since the data is not changed it can be considered
    // immutable no further assumptions are needed.
    private ConcurrentHashMap<String, Preview<RequestTransferEvidenceUSIDTType>> requestToPreview;
    private TaskScheduler taskScheduler;

    public PreviewStorage(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.requestToPreview = new ConcurrentHashMap<>();
        this.taskScheduler.scheduleWithFixedDelay(() -> this.pruneOld(Duration.ofMinutes(35)), Duration.ofSeconds(20));
    }

    private Preview<RequestTransferEvidenceUSIDTType> getRequestLockPair(String requestId) {
        if (!requestToPreview.containsKey(requestId)) {
            requestToPreview.put(requestId, new Preview<>(null, "", null));
        }
        return requestToPreview.get(requestId);
    }

    public void addRequestToPreview(RequestTransferEvidenceUSIDTType request, IDE4ACanonicalEvidenceType canonicalEvidenceType) {
        Preview<RequestTransferEvidenceUSIDTType> preview = getRequestLockPair(request.getRequestId());
        synchronized (preview.lock) {
            preview.object = request;
            preview.canonicalEvidenceType = canonicalEvidenceType;
            preview.lock.notifyAll();
        }
    }

    public CompletableFuture<RequestTransferEvidenceUSIDTType> getRequest(String requestId) throws InterruptedException {
        Preview<RequestTransferEvidenceUSIDTType> preview =  getRequestLockPair(requestId);
        while (requestToPreview.containsKey(requestId)) {
            synchronized (preview.lock) {
                if (preview.object != null) {
                    return CompletableFuture.completedFuture(preview.object);
                }
                log.debug("wait");
                preview.lock.wait();
            }
        }
        return CompletableFuture.failedFuture(new IOException("Preview pruned, too old"));
    }

    public IDE4ACanonicalEvidenceType getCanonicalEvidenceType(String requestId) {
        Preview<RequestTransferEvidenceUSIDTType> preview = getRequestLockPair(requestId);
        return preview.canonicalEvidenceType;
    }

    public List<String> getAllRequestIds() {
        return requestToPreview.entrySet().stream()
                .filter(entry -> entry.getValue().object != null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void setRedirectUrl(String requestId, String redirectionUrl) {
        Preview<RequestTransferEvidenceUSIDTType> preview = getRequestLockPair(requestId);
        preview.redirectionUrl = redirectionUrl;
    }

    public String getRedirectUrl(String requestId) {
        var preview = requestToPreview.get(requestId);
        if (preview == null) {
            return "";
        }
        return preview.redirectionUrl;
    }

    public void pruneOld(TemporalAmount oldThreshold) {
        requestToPreview.entrySet().stream()
                .filter(entry -> entry.getValue().timeStamp.plus(oldThreshold).isBefore(Instant.now()))
                .forEach(entry -> {
                    synchronized (entry.getValue().lock) {
                        requestToPreview.remove(entry.getKey());
                    }});
    }

    public void removePreview(String requestId) {
        requestToPreview.remove(requestId);
    }

    private static class Preview<T> {
        private final Object lock;
        private T object;
        private IDE4ACanonicalEvidenceType canonicalEvidenceType;
        private String redirectionUrl;
        private Instant timeStamp;

        private Preview(T object, String redirectionUrl, IDE4ACanonicalEvidenceType canonicalEvidenceType) {
            this.lock = new Object();
            this.object = object;
            this.canonicalEvidenceType = canonicalEvidenceType;
            this.timeStamp = Instant.now();
            this.redirectionUrl = redirectionUrl;
        }
    }
}
