package com.cisco.capture.packetanalyserservice.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.capture.packetanalyserservice.dto.PacketData;
import com.cisco.capture.packetanalyserservice.dto.PacketStatsDTO;
import com.cisco.capture.packetanalyserservice.service.PacketAnalyserService;

@RestController
@RequestMapping("/api")
public class PackerAnalyserController {

	private final PacketAnalyserService packetAnalyserService;

	public PackerAnalyserController(PacketAnalyserService packetAnalyserService) {
		this.packetAnalyserService = packetAnalyserService;
	}
	
	@GetMapping("/packets")
	public List<PacketData> getAllPackets() {
        return packetAnalyserService.getAllPackets();
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
}
