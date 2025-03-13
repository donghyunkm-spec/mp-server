package com.ktds.mvne.billing.config;

import com.ktds.mvne.billing.dto.BillingInfoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스입니다.
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    /**
     * Redis 연결 팩토리 빈을 생성합니다.
     *
     * @return RedisConnectionFactory 인스턴스
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        if (!redisPassword.isEmpty()) {
            redisConfig.setPassword(redisPassword);
        }
        return new LettuceConnectionFactory(redisConfig);
    }

    /**
     * 요금 정보를 저장하기 위한 RedisTemplate 빈을 생성합니다.
     *
     * @param connectionFactory RedisConnectionFactory 인스턴스
     * @return 요금 정보용 RedisTemplate 인스턴스
     */
    @Bean
    public RedisTemplate<String, BillingInfoResponseDTO> billingInfoRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, BillingInfoResponseDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
