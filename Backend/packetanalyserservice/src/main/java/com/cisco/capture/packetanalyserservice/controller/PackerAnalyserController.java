package com.cisco.capture.packetanalyserservice.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.capture.packetanalyserservice.dto.PacketData;
import com.cisco.capture.packetanalyserservice.dto.PacketStatsDTO;
import com.cisco.capture.packetanalyserservice.model.PacketEntity;
import com.cisco.capture.packetanalyserservice.service.PacketAnalyserService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PackerAnalyserController {

	private final PacketAnalyserService packetAnalyserService;

	public PackerAnalyserController(PacketAnalyserService packetAnalyserService) {
		this.packetAnalyserService = packetAnalyserService;
	}
	
	@GetMapping("/packets")
	public Page<PacketData> getAllPackets(
			@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return packetAnalyserService.getAllPackets(page, size);
    }

	@GetMapping("/packetcounts")
    public Map<String, Long> getProtocolCounts() {
        return packetAnalyserService.getPacketCountByProtocol();
    }
	
	@GetMapping("/graph")
    public List<PacketStatsDTO> getStats5Min() {
        // for now take last 24 hours
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusHours(24);
        return packetAnalyserService.getPacketStats5Min(start, end);
    }
	
	 @GetMapping("/anomalies")
	    public List<Map<String, Object>> getAnomalies() {
	        return packetAnalyserService.detectAnomalies();
	    }

	    // API: Export data
	    @GetMapping("/export")
	    public ResponseEntity<byte[]> exportData(@RequestParam(defaultValue = "json") String format) {
	        String result = packetAnalyserService.exportData(format);

	        if ("csv".equalsIgnoreCase(format)) {
	            return ResponseEntity.ok()
	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.csv")
	                    .contentType(MediaType.parseMediaType("text/csv"))
	                    .body(result.getBytes(StandardCharsets.UTF_8));
	        } else {
	            return ResponseEntity.ok()
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .body(result.getBytes(StandardCharsets.UTF_8));
	        }
	    }
	    
	    @GetMapping("/search")
	    public List<PacketEntity> searchPackets(
	            @RequestParam(required = false) String srcIp,
	            @RequestParam(required = false) String dstIp,
	            @RequestParam(required = false) String protocol,
	            @RequestParam(required = false) Integer srcPort,
	            @RequestParam(required = false) Integer dstPort,
	            @RequestParam(required = false)
	            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
	            @RequestParam(required = false)
	            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {

	        return packetAnalyserService.search(srcIp, dstIp, protocol, srcPort, dstPort, start, end);
	    }
}
