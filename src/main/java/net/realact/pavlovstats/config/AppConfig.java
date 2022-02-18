package net.realact.pavlovstats.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "appconfig")
public class AppConfig {
    private String rconHost;
    private int rconPort;
    private String rconPassword;
    private long rconCommandSleeps;
    private long rconTimeout;

    public String getRconHost() {
        return rconHost;
    }

    public void setRconHost(String rconHost) {
        this.rconHost = rconHost;
    }

    public int getRconPort() {
        return rconPort;
    }

    public void setRconPort(int rconPort) {
        this.rconPort = rconPort;
    }

    public String getRconPassword() {
        return rconPassword;
    }

    public void setRconPassword(String rconPassword) {
        this.rconPassword = rconPassword;
    }

    public long getRconCommandSleeps() {
        return rconCommandSleeps;
    }

    public void setRconCommandSleeps(long rconCommandSleeps) {
        this.rconCommandSleeps = rconCommandSleeps;
    }

    public long getRconTimeout() {
        return rconTimeout;
    }

    public void setRconTimeout(long rconTimeout) {
        this.rconTimeout = rconTimeout;
    }
}
