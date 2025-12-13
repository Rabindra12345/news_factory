package com.pironews.piropironews.heartbeat;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class HeartbeatJob {

    @Scheduled(fixedRate =14*20 * 1000)
    public void ping() {
        System.out.println("Heartbeat: ______________________________________:)" + java.time.Instant.now());
    }
}
