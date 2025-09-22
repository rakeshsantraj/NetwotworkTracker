package com.cisco.capture.packetanalyserservice.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	
	public Page<PacketData> getAllPackets(int page, int size) {
		Pageable pageable= PageRequest.of(page, size, Sort.by("timestamp").descending());
		Page<PacketEntity> peList= packetAnalyserRepository.findAll(pageable);
		//Page<PacketData> pdList= new Page<PacketData>();
		return peList.map((pe)->{
			PacketData pd= new PacketData();
			pd.setDstIp(pe.getDstIp());
			pd.setSrcIp(pe.getSrcIp());
			pd.setSrcPort(pe.getSrcPort());
			pd.setDstPort(pe.getDstPort());
			pd.setLength(pe.getLength());
			pd.setProtocol(pe.getProtocol());
			pd.setTimestamp(pe.getTimestamp());
			pd.setPayload(pe.getPayload());
			return pd;
		});
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
    
 // Detect anomalies
    public List<Map<String, Object>> detectAnomalies() {
        List<Map<String, Object>> anomalies = new ArrayList<>();
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        LocalDateTime thirtySecondsAgo = LocalDateTime.now().minusSeconds(30);

        // Detect port scans (more than 20 unique ports in 1 min)
        var portScanResults = packetAnalyserRepository.findUniquePortsPerIp(oneMinuteAgo);
        for (Object[] row : portScanResults) {
            String srcIp = (String) row[0];
            Long uniquePorts = (Long) row[1];
            if (uniquePorts > 20) {
                anomalies.add(Map.of(
                        "type", "PORT_SCAN",
                        "srcIp", srcIp,
                        "uniquePorts", uniquePorts,
                        "timeWindow", "1m"
                ));
            }
        }
        
        // Detect DNS floods (more than 100 queries in 30s)
        var dnsFloodResults = packetAnalyserRepository.findDnsQueriesPerIp(thirtySecondsAgo);
        for (Object[] row : dnsFloodResults) {
            String srcIp = (String) row[0];
            Long queries = (Long) row[1];
            if (queries > 100) {
                anomalies.add(Map.of(
                        "type", "DNS_FLOOD",
                        "srcIp", srcIp,
                        "queries", queries,
                        "timeWindow", "30s"
                ));
            }
        }

        return anomalies;
    }

    // Export CSV or JSON summary
    public String exportData(String format) {
        var data = packetAnalyserRepository.countPacketsByProtocol();

        if ("csv".equalsIgnoreCase(format)) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println("protocol,packets");
            for (Object[] row : data) {
                pw.println(row[0] + "," + row[1]);
            }
            return sw.toString();
        } else {
            // default JSON-like string
            List<Map<String, Object>> list = new ArrayList<>();
            for (Object[] row : data) {
                list.add(Map.of("protocol", row[0], "packets", row[1]));
            }
            return list.toString();
        }
    }
}
