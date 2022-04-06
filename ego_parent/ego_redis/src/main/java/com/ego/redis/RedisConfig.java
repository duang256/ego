package com.ego.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> redisTempldate = new RedisTemplate<>();
        redisTempldate.setKeySerializer(new StringRedisSerializer());
        redisTempldate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTempldate.setConnectionFactory(factory);
        return redisTempldate;
    }
}
