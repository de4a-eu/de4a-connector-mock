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

//import eu.de4a.iem.core.jaxb.common.RequestExtractMultiEvidenceUSIType;
import eu.de4a.iem.core.jaxb.common.ResponseExtractMultiEvidenceType;
import eu.de4a.iem.core.IDE4ACanonicalEvidenceType;
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
    private ConcurrentHashMap<String, Preview<ResponseExtractMultiEvidenceType>> requestToPreview;
    private TaskScheduler taskScheduler;

    public PreviewStorage(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        this.requestToPreview = new ConcurrentHashMap<>();
        this.taskScheduler.scheduleWithFixedDelay(() -> this.pruneOld(Duration.ofMinutes(35)), Duration.ofSeconds(20));
    }

    private Preview<ResponseExtractMultiEvidenceType> getRequestLockPair(String requestId) {
        if (!requestToPreview.containsKey(requestId)) {
            requestToPreview.put(requestId, new Preview<>(null, "", null));
        }
        return requestToPreview.get(requestId);
    }

    public void addRequestToPreview(ResponseExtractMultiEvidenceType res) {
        Preview<ResponseExtractMultiEvidenceType> preview = getRequestLockPair(res.getRequestId());
        synchronized (preview.lock) {
            preview.object = res;
            preview.lock.notifyAll();
        }
    }

    public CompletableFuture<ResponseExtractMultiEvidenceType> getRequest(String requestId) throws InterruptedException {
        Preview<ResponseExtractMultiEvidenceType> preview =  getRequestLockPair(requestId);
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

    private static class Preview<T> {
        private final Object lock;
        private ResponseExtractMultiEvidenceType object;
        private Instant timeStamp;

        private Preview(ResponseExtractMultiEvidenceType object, String redirectionUrl, IDE4ACanonicalEvidenceType canonicalEvidenceType) {
            this.lock = new Object();
            this.object = object;
            this.timeStamp = Instant.now();
        }
    }
}
