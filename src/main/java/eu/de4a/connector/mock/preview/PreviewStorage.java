package eu.de4a.connector.mock.preview;

import eu.de4a.iem.jaxb.common.types.RequestTransferEvidenceUSIDTType;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PreviewStorage {

    // For concurrency handling, I'm assuming the requestId is unique. Since the data is not changed it can be considered
    // immutable no further assumptions are needed.
    private ConcurrentHashMap<String, RequestTransferEvidenceUSIDTType> requestToPreview;
    // Makes sure it only try to query for missing evidence when a new evidence has been added
    private Object contLock;

    public PreviewStorage() {
        this.requestToPreview = new ConcurrentHashMap<>();
    }

    public void addRequestToPreview(RequestTransferEvidenceUSIDTType request) {
        requestToPreview.put(request.getRequestId(), request);
        contLock.notifyAll();
    }

    public CompletableFuture<RequestTransferEvidenceUSIDTType> getRequest(String requestId) throws InterruptedException {
        while (true) {
            if (requestToPreview.containsKey(requestId)) {
                return CompletableFuture.completedFuture(requestToPreview.get(requestId));
            }
            contLock.wait();
        }
    }

    public void removePreview(String requestId) {
        requestToPreview.remove(requestId);
    }

}
