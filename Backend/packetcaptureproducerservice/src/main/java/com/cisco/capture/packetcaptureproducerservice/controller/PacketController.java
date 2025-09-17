package com.cisco.capture.packetcaptureproducerservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/produce")
public class PacketController {

	private final KafkaTemplate<String, String> kafkaTemplate;
	
	@Value("${app.kafka.topic}")
	private String topic;
	
	public PacketController(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate= kafkaTemplate;
	}
	
	@PostMapping
	public ResponseEntity<String> neworkProducer(@RequestBody Map<String, Object> packet){
		try {
			String message= new ObjectMapper().writeValueAsString(packet);
			kafkaTemplate.send(topic, message);
			return ResponseEntity.ok("Packet sent to Kafka ✅");
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return ResponseEntity.status(500).body("Failed to send packet ❌: " + e.getMessage());
		}
	}
}
