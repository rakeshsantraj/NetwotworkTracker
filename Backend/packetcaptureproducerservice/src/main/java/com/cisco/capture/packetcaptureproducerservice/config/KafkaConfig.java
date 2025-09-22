package com.cisco.capture.packetcaptureproducerservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

	@Bean
	public NewTopic packetTopic() {
		return TopicBuilder
				.name("packets1")
				.partitions(3)
				.replicas(3)
				.build();
	}
}
