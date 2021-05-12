package eu.de4a.connector.mock.preview;

import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@Profile("do")
public class PreviewStorage {

    // For concurrency handling, I'm assuming the requestId is unique. Since the data is not changed it can be considered
    // immutable no further assumptions are needed.
    private ConcurrentHashMap<String, Pair<Object, RequestTransferEvidenceUSIDTType>> requestToPreview;
    private TaskScheduler taskScheduler;

    public PreviewStorage(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.requestToPreview = new ConcurrentHashMap<>();
        this.taskScheduler.scheduleWithFixedDelay(() -> this.pruneOld(Duration.ofHours(1)), Duration.ofMinutes(1));
    }

    private Pair<Object, RequestTransferEvidenceUSIDTType> getRequestLockPair(String requestId) {
        if (!requestToPreview.containsKey(requestId)) {
            requestToPreview.put(requestId, new Pair<>(new Object(), null));
        }
        return requestToPreview.get(requestId);
    }

    public void addRequestToPreview(RequestTransferEvidenceUSIDTType request) {
        Pair<Object, RequestTransferEvidenceUSIDTType> pair = getRequestLockPair(request.getRequestId());
        synchronized (pair.lock) {
            pair.object = request;
            pair.lock.notifyAll();
        }
    }

    public CompletableFuture<RequestTransferEvidenceUSIDTType> getRequest(String requestId) throws InterruptedException {
        Pair<Object, RequestTransferEvidenceUSIDTType> pair =  getRequestLockPair(requestId);
        while (requestToPreview.containsKey(requestId)) {
            synchronized (pair.lock) {
                if (pair.object != null) {
                    return CompletableFuture.completedFuture(pair.object);
                }
                log.debug("wait");
                pair.lock.wait();
            }
        }
        return CompletableFuture.failedFuture(new IOException("Preview pruned, too old"));
    }

    public List<String> getAllRequestIds() {
        return requestToPreview.entrySet().stream()
                .filter(entry -> entry.getValue().object != null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
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

    private static class Pair<Lock, Object> {
        private final Lock lock;
        private Object object;
        private Instant timeStamp;

        private Pair(Lock lock, Object object) {
            this.lock = lock;
            this.object = object;
            this.timeStamp = Instant.now();
        }
    }
}
