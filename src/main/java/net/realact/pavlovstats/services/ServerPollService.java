package net.realact.pavlovstats.services;

import org.springframework.scheduling.annotation.Scheduled;

public interface ServerPollService {
    @Scheduled(fixedDelayString = "${appconfig.server-polling-interva}")
    void poll();
}
