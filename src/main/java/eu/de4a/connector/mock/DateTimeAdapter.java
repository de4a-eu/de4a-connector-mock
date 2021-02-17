package eu.de4a.connector.mock;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import com.helger.commons.datetime.PDTWebDateHelper;

@Slf4j
public class DateTimeAdapter extends XmlAdapter<String, LocalDateTime> {


    @Override
    public LocalDateTime unmarshal(String s) throws Exception {
        LocalDateTime localDateTime = PDTWebDateHelper.getLocalDateTimeFromXSD(s);
        return localDateTime;
    }

    @Override
    public String marshal(LocalDateTime localDateTime) throws Exception {
        return PDTWebDateHelper.getAsStringXSD(localDateTime);
    }

}
