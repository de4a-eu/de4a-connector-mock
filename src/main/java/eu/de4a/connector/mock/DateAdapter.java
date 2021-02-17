package eu.de4a.connector.mock;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateAdapter extends XmlAdapter<String, LocalDateTime> {


    @Override
    public LocalDateTime unmarshal(String s) throws Exception {
        LocalDateTime localDateTime = LocalDateTime.parse(s, DateTimeFormatter.ISO_INSTANT);
        log.debug("localDateTime: {} from {}", localDateTime.format(DateTimeFormatter.ISO_INSTANT), s);
        return localDateTime;
    }

    @Override
    public String marshal(LocalDateTime localDateTime) throws Exception {
        return localDateTime.format(DateTimeFormatter.ISO_INSTANT);
    }

}
