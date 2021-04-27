package eu.de4a.connector.mock.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

public class DOConfig implements Serializable {

    @Getter
    @Value("${mock.do.preview.endpoint}")
    private String previewEndpoint;
    @Getter
    @Value("${mock.do.preview.evidence.get.endpoint}")
    private String previewEvidenceRequest;
    @Getter
    @Value("${mock.do.preview.evidence.timeout}")
    private long previewEvidenceTimeout;
    @Getter
    @Value("${mock.do.preview.evidence.accept.endpoint}")
    private String previewEvidenceAccept;
    @Getter
    @Value("${mock.do.preview.evidence.reject.endpoint}")
    private String previewEvidenceReject;
    @Getter
    @Value("${mock.do.preview.dt.url}")
    private String previewDTUrl;

}
