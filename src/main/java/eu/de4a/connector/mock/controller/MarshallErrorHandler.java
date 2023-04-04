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
