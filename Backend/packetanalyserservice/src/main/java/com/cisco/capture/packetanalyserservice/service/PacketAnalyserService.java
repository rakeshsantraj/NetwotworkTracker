package com.cisco.capture.packetanalyserservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cisco.capture.packetanalyserservice.Repository.PacketAnalyserRepository;
import com.cisco.capture.packetanalyserservice.dto.PacketData;
import com.cisco.capture.packetanalyserservice.dto.PacketStatsDTO;
import com.cisco.capture.packetanalyserservice.model.PacketEntity;

@Service
public class PacketAnalyserService {

	private PacketAnalyserRepository packetAnalyserRepository;

	public PacketAnalyserService(PacketAnalyserRepository packetAnalyserRepository) {
		this.packetAnalyserRepository = packetAnalyserRepository;
	}
	
	public List<PacketData> getAllPackets() {
		List<PacketEntity> peList= packetAnalyserRepository.findAll();
		List<PacketData> pdList= new ArrayList<PacketData>();
		for(PacketEntity pe: peList) {
			PacketData pd= new PacketData();
			pd.setDstIp(pe.getDstIp());
			pd.setSrcIp(pe.getSrcIp());
			pd.setSrcPort(pe.getSrcPort());
			pd.setDstPort(pe.getDstPort());
			pd.setLength(pe.getLength());
			pd.setProtocol(pe.getProtocol());
			pd.setTimestamp(pe.getTimestamp());
			pd.setPayload(pe.getPayload());
			pdList.add(pd);
		}
        return pdList;
    }
	
	public Map<String, Long> getPacketCountByProtocol() {
        List<Object[]> results = packetAnalyserRepository.countPacketsByProtocol();
        Map<String, Long> response = new HashMap<>();

        for (Object[] row : results) {
            String protocol = (String) row[0];
            Long count = (Long) row[1];
            response.put(protocol, count);
        }

        return response;
    }
	
	
    public List<PacketStatsDTO> getPacketStats5Min(LocalDateTime start, LocalDateTime end) {
        List<PacketEntity> packets = packetAnalyserRepository.findByTimestampBetween(start, end);

        // Group packets into 5-minute buckets
        Map<LocalDateTime, Long> grouped = packets.stream()
                .collect(Collectors.groupingBy(
                        p -> truncateTo5Minutes(p.getTimestamp()),
                        Collectors.counting()
                ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new PacketStatsDTO(
                        e.getKey().format(formatter),
                        e.getValue()
                ))
                .collect(Collectors.toList());
    }

    private LocalDateTime truncateTo5Minutes(LocalDateTime timestamp) {
        int minute = timestamp.getMinute();
        int bucketMinute = (minute / 5) * 5; // round down to nearest multiple of 5
        return timestamp.withMinute(bucketMinute).withSecond(0).withNano(0);
    }
}
