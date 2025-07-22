package shop.dodream.front.util;

import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component("dateUtil")
public class DateUtil {
    public String toKst(ZonedDateTime time, String pattern) {
        if(time == null){
            return "";
        }
        return time.withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern(pattern));
    }
}
