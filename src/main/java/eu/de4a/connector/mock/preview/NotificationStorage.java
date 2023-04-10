/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import eu.de4a.iem.core.jaxb.common.EventNotificationType;
import eu.de4a.iem.core.jaxb.common.ResponseEventSubscriptionType;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile("do")
public class NotificationStorage {
// TODO 
    // For concurrency handling, I'm assuming the requestId is unique. Since the data is not changed it can be considered
    // immutable no further assumptions are needed.
    private ConcurrentHashMap<String, Preview<EventNotificationType>> notificationToPreview;
    private TaskScheduler taskScheduler;

    public NotificationStorage(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.notificationToPreview = new ConcurrentHashMap<>();
        this.taskScheduler.scheduleWithFixedDelay(() -> this.pruneOld(Duration.ofMinutes(35)), Duration.ofSeconds(20));
    }

    private Preview<EventNotificationType> getRequestLockPair(String requestId) {
        if (!notificationToPreview.containsKey(requestId)) {
        	notificationToPreview.put(requestId, new Preview<>(null, "", null));
        }
        return notificationToPreview.get(requestId);
    }

    public void addRequestToPreview(EventNotificationType request) {
        Preview<EventNotificationType> preview = getRequestLockPair(request.getNotificationId());
        synchronized (preview.lock) {
            preview.object = request;
            preview.lock.notifyAll();
        }
    }

    public CompletableFuture<EventNotificationType> getRequest(String requestId) throws InterruptedException {
        Preview<EventNotificationType> preview =  getRequestLockPair(requestId);
        while (notificationToPreview.containsKey(requestId)) {
            synchronized (preview.lock) {
                if (preview.object != null) {
                    return CompletableFuture.completedFuture(preview.object);
                }
                log.debug("wait");
                preview.lock.wait();
            }
        }
        return CompletableFuture.failedFuture(new IOException("notification preview pruned, too old"));
    }
    
    public CompletableFuture<EventNotificationType> getNotification(String requestId) throws InterruptedException {
    	Preview<EventNotificationType> preview =  getRequestLockPair(requestId);
        while (notificationToPreview.containsKey(requestId)) {
            synchronized (preview.lock) {
                if (preview.object != null) {
                    return CompletableFuture.completedFuture(preview.object);
                }
                log.debug("wait");
                preview.lock.wait();
            }
        }
        return CompletableFuture.failedFuture(new IOException("notification preview pruned, too old"));
	}

    public List<String> getAllRequestIds() {
        return notificationToPreview.entrySet().stream()
                .filter(entry -> entry.getValue().object != null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void pruneOld(TemporalAmount oldThreshold) {
    	notificationToPreview.entrySet().stream()
                .filter(entry -> entry.getValue().timeStamp.plus(oldThreshold).isBefore(Instant.now()))
                .forEach(entry -> {
                    synchronized (entry.getValue().lock) {
                    	notificationToPreview.remove(entry.getKey());
                    }});
    }

    public void removePreview(String requestId) {
    	notificationToPreview.remove(requestId);
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
