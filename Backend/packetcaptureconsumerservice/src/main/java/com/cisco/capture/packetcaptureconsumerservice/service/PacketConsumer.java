package com.cisco.capture.packetcaptureconsumerservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.cisco.capture.packetcaptureconsumerservice.dto.PacketData;
import com.cisco.capture.packetcaptureconsumerservice.model.PacketEntity;
import com.cisco.capture.packetcaptureconsumerservice.repository.PacketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PacketConsumer {

	private final PacketRepository packetRepository;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private final ObjectMapper objectMapper;
	
	public PacketConsumer(PacketRepository packetRepository, ObjectMapper objectMapper) {
		this.packetRepository= packetRepository;
		this.objectMapper= objectMapper;
	}
	
	@KafkaListener(topics = "packets", groupId = "packet-consumer-group")
	public void consume(String message) throws Exception {
		PacketData packet = objectMapper.readValue(message, PacketData.class);

	    PacketEntity entity = new PacketEntity();
	    entity.setSrcIp(packet.getSrcIp());
	    entity.setDstIp(packet.getDstIp());
	    entity.setProtocol(packet.getProtocol());
	    entity.setLength(packet.getLength());
	    entity.setSrcPort(packet.getSrcPort());
	    entity.setDstPort(packet.getDstPort());
	    entity.setPayload(packet.getPayload());
	    entity.setTimestamp(packet.getTimestamp()); // already LocalDateTime
	    packetRepository.save(entity);

	    System.out.println("✅ Saved packet → " + entity);
    }
	
}
