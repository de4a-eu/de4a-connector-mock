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
package eu.de4a.connector.mock.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import java.io.Serializable;

@Profile("do")
public class DOConfig implements Serializable {

	private StringBuilder ret; 
	
    @Getter
    @Value("${mock.do.preview.endpoint.base}")
    private String previewBaseEndpoint;
    @Getter
    @Value("${mock.do.preview.endpoint.index}")
    private String indexEndpoint;
    @Getter
    @Value("${mock.do.preview.evidence.get.endpoint}")
    private String previewEvidenceRequest;
    @Getter
    @Value("${mock.do.preview.evidence.accept.endpoint}")
    private String previewEvidenceAccept;
    @Getter
    @Value("${mock.do.preview.evidence.reject.endpoint}")
    private String previewEvidenceReject;
    @Getter
    @Value("${mock.do.preview.dt.url}")
    private String previewDTUrl;
    @Getter
    @Value("${mock.do.preview.endpoint.websocket.mess}")
    private String websocketMessagesEndpoint;
    @Getter
    @Value("${mock.do.preview.endpoint.websocket.socket}")
    private String websocketSocketEndpoint;
    @Getter
    @Value("${mock.do.preview.evidence.requestId.all.endpoint}")
    private String previewRequestIdsEndpoint;
    @Getter
    @Value("${mock.do.preview.evidence.redirecturl.endpoint}")
    private String previewRedirectUrlEndpoint;
    @Getter
    @Value("${mock.do.preview.bundle.path}")
    private String bundlePath;
    @Getter
    @Value("${mock.do.preview.endpoint.subscription.base}")
    private String subscriptionBaseEndpoint;
    @Getter
    @Value("${mock.do.preview.endpoint.subscription.index}")
    private String subscriptionEndpoint;
    @Getter
    @Value("${mock.do.preview.subscription.requestId.all.endpoint}")
    private String previewSubscriptionRequestIdsEndpoint;
    @Getter
    @Value("${mock.do.preview.subscription.get.endpoint}")
    private String previewSubscriptionRequest;
    
    @Getter
    @Value("${mock.do.preview.endpoint.notification}")
    private String notificationEndpoint;
    
    @Getter
    @Value("${mock.do.create.notification}")
    private String createNotif;
    
    @Getter
    @Value("${mock.do.send.notification}")
    private String sendNotif;
    
    @Getter
    @Value("${mock.do.send.notif.subscrip}")
    private String sendNotifFromSubscrip;

    @Getter
    @Value("${mock.do.build.notif.subscrip}")
    private String buildNotifFromSubscrip;
    
    @Getter
    @Value("${mock.do.dt.notification.url}")
    private String notificationURL;
    
    @Getter
    @Value("${mock.do.dt.usi.url}")
    private String usiURL;
    
    @Getter
    @Value("${mock.do.dt.evidence.url}")
    private String evidenceURL;
    
    @Getter
    @Value("${mock.do.dt.im.url}")
    private String imURL;
    
    @Getter
    @Value("${mock.do.preview.dt.redirect.url}")
    private String redirectDTURL;
    
    @Getter
    @Value("${mock.do.preview.dt.eventsubscription.url}")
    private String eventSubscriptionURL;
    
	public String getDTUrlNotification() {
		ret = new StringBuilder();
		return ret.append(previewDTUrl).append(notificationURL).toString();
	}
	
	public String getDTUrlUSI() {
		ret = new StringBuilder();
		return ret.append(previewDTUrl).append(usiURL).toString();
	}
	
	public String getDTUrlIM() {
		ret = new StringBuilder();
		return ret.append(previewDTUrl).append(imURL).toString();
	}
	
	public String getDTEvidenceUrl() {
		ret = new StringBuilder();
		return ret.append(previewDTUrl).append(evidenceURL).toString();
	}
	
    /*
    @Getter
    @Value("${mock.do.preview.endpoint.notification.summary}")
    private String notificationSummary;
    */

}
