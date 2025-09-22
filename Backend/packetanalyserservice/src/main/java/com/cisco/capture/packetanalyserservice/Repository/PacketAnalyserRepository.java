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
    
 // Count unique destination ports per source IP in a time window
    @Query("SELECT p.srcIp, COUNT(DISTINCT p.dstPort) " +
           "FROM PacketEntity p " +
           "WHERE p.timestamp >= :since " +
           "GROUP BY p.srcIp")
    List<Object[]> findUniquePortsPerIp(LocalDateTime since);

    // Count DNS queries per source IP in a time window
    @Query("SELECT p.srcIp, COUNT(p) " +
           "FROM PacketEntity p " +
           "WHERE p.protocol = 'DNS' AND p.timestamp >= :since " +
           "GROUP BY p.srcIp")
    List<Object[]> findDnsQueriesPerIp(LocalDateTime since);

    // Aggregate summary for export (packets per protocol)
    @Query("SELECT p.protocol, COUNT(p) FROM PacketEntity p GROUP BY p.protocol")
    List<Object[]> aggregateByProtocol();
}
