package com.cisco.capture.packetcaptureproducerservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

	@Value("${spring.kafka.admin.properties.replication.factor}")
	private int replicationFactor;
	
	@Bean
	public NewTopic packetTopic() {
		return TopicBuilder
				.name("packets1")
				.partitions(3)
				.replicas(replicationFactor)
				.build();
	}
}
