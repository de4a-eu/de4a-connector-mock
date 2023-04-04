package eu.de4a.connector.mock.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.helger.commons.collection.ArrayHelper;

import eu.de4a.connector.mock.StaticContextAccessor;
import eu.de4a.iem.core.DE4AResponseDocumentHelper;
import eu.de4a.iem.core.jaxb.common.ErrorType;
import eu.de4a.kafkaclient.model.ELogMessage;

@Component
public class MessageUtils {
	
	@Value("${de4a.connector.id}")
    private String connectorIdentifier;

    private static String CONNECTOR_ID;

    @Value("${de4a.connector.id}")
    public void setNameStatic(String connectorIdentifier){
    	MessageUtils.CONNECTOR_ID = connectorIdentifier;
    }
    
    public static String getConnectorId() {
        return MessageUtils.CONNECTOR_ID;
    }
    
    public static ErrorType GetErrorType(final ELogMessage logMessage, String useCase, String detail) {
    	String errorCode = logMessage.getLogCode();
        String errorMessage = getErrorMessage(logMessage, useCase, detail);
        return DE4AResponseDocumentHelper.createError(errorCode, errorMessage);
    }
	
    public static String getErrorMessage(final ELogMessage logMessage, String... params) {
    	ArrayList<Object> listParams = new ArrayList<Object>(Arrays.asList(params));
    	listParams.add(0,  "[" + logMessage.getLogCode() +"] ["+ MessageUtils.CONNECTOR_ID + "]");
        return MessageUtils.format(logMessage.getKey(), listParams.toArray());
    }
    
    public static String format(final String key, final Object[] args) {
        final MessageSource messageSource = StaticContextAccessor.getBean(MessageSource.class);
        final Locale locale = LocaleContextHolder.getLocale();
        if(args != null && args.length > 0) {
            return messageSource.getMessage(key, args, locale);
        }
        return messageSource.getMessage(key, ArrayHelper.EMPTY_OBJECT_ARRAY, locale);
    }
}
