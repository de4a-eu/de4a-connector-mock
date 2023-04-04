package eu.de4a.connector.mock.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.helger.commons.error.level.EErrorLevel;

import eu.de4a.kafkaclient.DE4AKafkaClient;
import eu.de4a.kafkaclient.model.ELogMessage;

@Component
public class KafkaClientWrapper {

    @Value("${de4a.connector.id}")
    private String connectorIdentifier;

    private static String CONNECTOR_ID;

    @Value("${de4a.connector.id}")
    public void setNameStatic(String connectorIdentifier){
    	KafkaClientWrapper.CONNECTOR_ID = connectorIdentifier;
    }

    private KafkaClientWrapper (){}

    public static void sendInfo(final ELogMessage logMessage, final Object...params) {
        _send(logMessage, EErrorLevel.INFO, params);
    }

    public static void sendSuccess(final ELogMessage logMessage, final Object...params) {
        _send(logMessage, EErrorLevel.SUCCESS, params);
    }

    public static void sendWarn(final ELogMessage logMessage, final Object...params) {
        _send(logMessage, EErrorLevel.WARN, params);
    }

    public static void sendError(final ELogMessage logMessage, final Object...params) {
        _send(logMessage, EErrorLevel.ERROR, params);
    }

    private static void _send(final ELogMessage logMessage, final EErrorLevel level, Object...params) {
    	ArrayList<Object> listParams = new ArrayList<Object>(Arrays.asList(params));
    	listParams.add(0,  "[" + logMessage.getLogCode() +"] ["+ KafkaClientWrapper.CONNECTOR_ID + "]");
        final String msg = MessageUtils.format(logMessage.getKey(), listParams.toArray());
        CompletableFuture.runAsync(() -> DE4AKafkaClient.send(level, msg));
    }
}
