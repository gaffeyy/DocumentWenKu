package com.wenku.documents_wenku.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类
 *
 * @author GaffEy
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissionConfig {

	private String host;
	private String port;

	@Bean
	public RedissonClient redissonClient() {
		// 1. 创建配置
		Config config = new Config();
		String redisAddress = String.format("redis://%s:%s", host, port);
		config.useSingleServer()
				.setPassword("4527466")
				.setAddress(redisAddress)
				.setDatabase(3);
		// 2. 创建实例
		RedissonClient redisson = Redisson.create(config);
		return redisson;
	}

}
