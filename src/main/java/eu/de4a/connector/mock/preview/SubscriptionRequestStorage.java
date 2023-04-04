package eu.de4a.connector.mock.preview;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
import eu.de4a.iem.core.jaxb.common.RequestEventSubscriptionType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionType;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile("do")
public class SubscriptionRequestStorage {
// TODO 
    // For concurrency handling, I'm assuming the requestId is unique. Since the data is not changed it can be considered
    // immutable no further assumptions are needed.
    private ConcurrentHashMap<String, Preview<RequestEventSubscriptionType>> savedSubscription;
    private TaskScheduler taskScheduler;

    public SubscriptionRequestStorage(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.savedSubscription = new ConcurrentHashMap<>();
        this.taskScheduler.scheduleWithFixedDelay(() -> this.pruneOld(Duration.ofMinutes(35)), Duration.ofSeconds(20));
    }

    private Preview<RequestEventSubscriptionType> getRequestLockPair(String requestId) {
        if (!savedSubscription.containsKey(requestId)) {
        	savedSubscription.put(requestId, new Preview<>(null, "", null));
        }
        return savedSubscription.get(requestId);
    }

    public void saveRequest(RequestEventSubscriptionType request) {
        Preview<RequestEventSubscriptionType> preview = getRequestLockPair(request.getRequestId());
        synchronized (preview.lock) {
            preview.object = request;
            preview.lock.notifyAll();
        }
    }

    public CompletableFuture<RequestEventSubscriptionType> getRequest(String requestId) throws InterruptedException {
        Preview<RequestEventSubscriptionType> preview =  getRequestLockPair(requestId);
        while (savedSubscription.containsKey(requestId)) {
            synchronized (preview.lock) {
                if (preview.object != null) {
                    return CompletableFuture.completedFuture(preview.object);
                }
                log.debug("wait");
                preview.lock.wait();
            }
        }
        return CompletableFuture.failedFuture(new IOException("Subscription preview pruned, too old"));
    }

    public List<String> getAllRequestIds() {
        return savedSubscription.entrySet().stream()
                .filter(entry -> entry.getValue().object != null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void pruneOld(TemporalAmount oldThreshold) {
    	savedSubscription.entrySet().stream()
                .filter(entry -> entry.getValue().timeStamp.plus(oldThreshold).isBefore(Instant.now()))
                .forEach(entry -> {
                    synchronized (entry.getValue().lock) {
                    	savedSubscription.remove(entry.getKey());
                    }});
    }

    public void remove(String requestId) {
    	savedSubscription.remove(requestId);
    }

    private static class Preview<T> {
        private final Object lock;
        private T object;
        private Instant timeStamp;

        private Preview(T object, String redirectionUrl, IDE4ACanonicalEvidenceType canonicalEvidenceType) {
            this.lock = new Object();
            this.object = object;
            this.timeStamp = Instant.now();
        }
    }
}
