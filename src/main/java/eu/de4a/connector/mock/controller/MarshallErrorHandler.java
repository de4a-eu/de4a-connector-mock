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
package eu.de4a.connector.mock.controller;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBException;

public class MarshallErrorHandler {

    private static MarshallErrorHandler instance;

    final private ConcurrentHashMap<UUID, JAXBException> errorHashMap;

    private MarshallErrorHandler() {
        this.errorHashMap = new ConcurrentHashMap<>();
    }

    public static MarshallErrorHandler getInstance() {
        if (instance == null) {
            instance = new MarshallErrorHandler();
        }
        return instance;
    }

    public void postError(UUID key, JAXBException ex) {
        errorHashMap.put(key, ex);
    }

    public CompletableFuture<JAXBException> getError(UUID key) throws InterruptedException{
        //I will assume getError will not be called from multiple threads with the same key.
        //Therefore no locks are needed.
        while (true) {
            if (errorHashMap.containsKey(key)) {
                JAXBException ex = errorHashMap.get(key);
                errorHashMap.remove(key);
                return CompletableFuture.completedFuture(ex);
            }
        }
    }
}
