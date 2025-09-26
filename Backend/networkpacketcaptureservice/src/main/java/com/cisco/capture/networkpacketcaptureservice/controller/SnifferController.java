package com.cisco.capture.networkpacketcaptureservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cisco.capture.networkpacketcaptureservice.dto.PacketData;
import com.cisco.capture.networkpacketcaptureservice.service.SnifferService;

@RestController
@RequestMapping("/api/v1/sniffer")
public class SnifferController {

    private final SnifferService snifferService;

    public SnifferController(SnifferService snifferService) {
        this.snifferService = snifferService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> start() {
    	try {
    		snifferService.start();
            return ResponseEntity.ok(Map.of(
                    "status", "started",
                    "running", snifferService.isRunning(),
                    "captured", snifferService.getCapturedCount()
            ));
    	}catch (Exception e) {
    		 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body(Map.of(
                             "status", "error",
                             "message", e.getMessage(),
                             "running", snifferService.isRunning(),
                             "captured", snifferService.getCapturedCount()
                     ));
		}
        
    }

    @PostMapping("/stop")
    public ResponseEntity<Map<String, Object>> stop() {
    	try {
    		snifferService.stop();
            return ResponseEntity.ok(Map.of(
                    "status", "stopped",
                    "running", snifferService.isRunning(),
                    "captured", snifferService.getCapturedCount()
            ));
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", e.getMessage(),
                            "running", snifferService.isRunning(),
                            "captured", snifferService.getCapturedCount()
                    ));
    	}
        
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of(
                "running", snifferService.isRunning(),
                "captured", snifferService.getCapturedCount()
        ));
    }
    
    @GetMapping("/packets")
    public ResponseEntity<List<PacketData>> packets() {
        return ResponseEntity.ok(snifferService.getCapturedPackets());
    }
}