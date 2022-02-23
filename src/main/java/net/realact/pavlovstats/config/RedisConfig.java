package net.realact.pavlovstats.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

import java.time.Duration;

@Configuration
public class RedisConfig {
    private RedisServer redisServer;
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(
            AppConfig appConfig) {

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        config.setHostName(appConfig.getRedisHost());
        config.setPort(appConfig.getRedisPort());
        config.setPassword(appConfig.getRedisPassword());

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("responseCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)));
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public Object startRedis(AppConfig appConfig){
        if("localhost".equalsIgnoreCase(appConfig.getRedisHost()) && appConfig.getRedisPort() != RedisURI.DEFAULT_REDIS_PORT){
            RedisServerBuilder builder = RedisServer.builder()
                    .port(appConfig.getRedisPort())
                    .setting("appendonly yes")
                    .setting("appendfsync everysec")
                    .setting("maxheap 1024M");
            if(appConfig.getRedisWorkingDir() != null && appConfig.getRedisWorkingDir().isEmpty() == false){
                builder.setting("dir " + appConfig.getRedisWorkingDir());
            }
            if(appConfig.getRedisPassword() != null && appConfig.getRedisPassword().isEmpty() == false){
                builder.setting("requirepass " + appConfig.getRedisPassword());
            }
            redisServer = builder.build();
            LOGGER.info("Starting embedded Redis on port {}", appConfig.getRedisPort());
            redisServer.start();
        }
        return new Object();
    }

    public void stopRedis(){
        if(redisServer != null){
            redisServer.stop();
        }
    }
}