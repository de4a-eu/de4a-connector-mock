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
package eu.de4a.connector.mock;

import eu.de4a.kafkaclient.DE4AKafkaSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.helger.httpclient.HttpClientSettings;	

@Component
public class MockStartupRunner implements ApplicationRunner {

    @Value("${mock.kafka.enabled:false}")
    private boolean kafka_enabled;
    @Value("${mock.kafka.url:'de4a-dev-kafka.egovlab.eu:9092'}")
    private String kafka_url;
    @Value("${mock.kafka.topic:'de4a-mock'}")
    private String kafka_topic;
    @Value("${mock.kafka.http:false}")
    private boolean kafka_http;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        DE4AKafkaSettings.defaultProperties().put("bootstrap.servers", kafka_url);
        DE4AKafkaSettings.setKafkaEnabled(kafka_enabled);
        DE4AKafkaSettings.setLoggingEnabled(kafka_enabled);
        DE4AKafkaSettings.setKafkaTopic(kafka_topic);
	DE4AKafkaSettings.setKafkaHttp(kafka_http);
	if(kafka_http){
	    // TODO Maybe add some relevant settings but leaving this out for now. Proxy settings would be releveant for instance...
	    DE4AKafkaSettings.setHttpClientSetting(new HttpClientSettings());
	}
    }
}
