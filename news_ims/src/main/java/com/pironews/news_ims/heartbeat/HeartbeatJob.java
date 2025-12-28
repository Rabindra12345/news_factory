package com.pironews.news_ims.heartbeat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class HeartbeatJob {

    @Value("${time.dateTimeZone}")
    String dateTimeZone;

    @Scheduled(fixedRate =13*60* 1000)
    public void ping() {
        System.out.println("Heartbeat: ______________________________________:)" + ZonedDateTime.now(ZoneId.of(dateTimeZone)).toLocalDateTime());
    }
}
