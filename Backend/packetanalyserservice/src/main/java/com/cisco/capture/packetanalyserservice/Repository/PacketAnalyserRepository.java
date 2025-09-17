package com.cisco.capture.packetanalyserservice.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cisco.capture.packetanalyserservice.model.PacketEntity;

public interface PacketAnalyserRepository extends JpaRepository<PacketEntity, Long> {

	@Query("SELECT p.protocol, COUNT(p) FROM PacketEntity p GROUP BY p.protocol")
    List<Object[]> countPacketsByProtocol();
    
    List<PacketEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
