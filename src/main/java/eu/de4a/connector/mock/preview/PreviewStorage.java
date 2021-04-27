package eu.de4a.connector.mock.preview;

import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class PreviewStorage {

    // For concurrency handling, I'm assuming the requestId is unique. Since the data is not changed it can be considered
    // immutable no further assumptions are needed.
    private ConcurrentHashMap<String, Pair<Object, RequestTransferEvidenceUSIDTType>> requestToPreview;
    @Autowired
    private TaskScheduler taskScheduler;

    public PreviewStorage() {
        this.requestToPreview = new ConcurrentHashMap<>();
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
        while (true) {
            synchronized (pair.lock) {
                if (pair.object != null) {
                    return CompletableFuture.completedFuture(pair.object);
                }
                log.debug("wait");
                pair.lock.wait();
            }
        }
    }

    public void removePreview(String requestId) {
        requestToPreview.remove(requestId);
    }

    private static class Pair<Lock, Object> {
        private final Lock lock;
        private Object object;

        private Pair(Lock lock, Object object) {
            this.lock = lock;
            this.object = object;
        }
    }
}
