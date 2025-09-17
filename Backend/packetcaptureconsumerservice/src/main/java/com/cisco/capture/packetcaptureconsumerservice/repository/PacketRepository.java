package com.cisco.capture.packetcaptureconsumerservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.capture.packetcaptureconsumerservice.model.PacketEntity;

public interface PacketRepository extends JpaRepository<PacketEntity, Long>{

}
