package net.realact.pavlovstats.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConfigurationProperties(prefix = "appconfig")
@EnableScheduling
public class AppConfig {
    private String rconHost;
    private int rconPort;
    private String rconPassword;
    private long rconCommandSleeps;
    private long rconTimeout;
    private int serverPollingInterval;

    private int redisPort;
    private String redisHost;
    private String redisPassword;
    private String redisWorkingDir;
    private String redisHashSuffix;

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

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public void setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
    }

    public String getRedisWorkingDir() {
        return redisWorkingDir;
    }

    public void setRedisWorkingDir(String redisWorkingDir) {
        this.redisWorkingDir = redisWorkingDir;
    }

    public int getServerPollingInterval() {
        return serverPollingInterval;
    }

    public void setServerPollingInterval(int serverPollingInterval) {
        this.serverPollingInterval = serverPollingInterval;
    }

    public String getRedisHashSuffix() {
        return redisHashSuffix;
    }

    public void setRedisHashSuffix(String redisHashSuffix) {
        this.redisHashSuffix = redisHashSuffix;
    }
}
