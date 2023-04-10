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
