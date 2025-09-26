package com.cisco.capture.networkpacketcaptureservice.service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.pcap4j.packet.namednumber.IpNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cisco.capture.networkpacketcaptureservice.config.AppProperties;
import com.cisco.capture.networkpacketcaptureservice.config.ProducerClient;
import com.cisco.capture.networkpacketcaptureservice.dto.PacketData;

import jakarta.annotation.PreDestroy;

@Service
public class SnifferService {

    private static final Logger logger = LoggerFactory.getLogger(SnifferService.class);

    private final AppProperties props;
    private final ProducerClient producerClient;

    // Track all active handles for cleanup
    private final List<PcapHandle> handles = new ArrayList<>();
    private final List<PacketData> captured = new CopyOnWriteArrayList<>();

    // Use cached pool so each interface can run its own thread
    private final ExecutorService snifferExecutor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "pcap-sniffer-thread");
        t.setDaemon(true);
        return t;
    });

    private final BlockingQueue<PacketData> queue = new LinkedBlockingQueue<>(10000);
    private volatile boolean running = false;
    private final Object lifecycleLock = new Object();

    public SnifferService(AppProperties props, ProducerClient producerClient) {
        this.props = props;
        this.producerClient = producerClient;
    }

    // ===== Lifecycle =====
    public void start() {
        System.out.println("Producer URL: " + props.getProducerUrl());
        System.out.println("Interface: " + props.getCaptureInterface());
        System.out.println("Log File: " + props.getLogFile());

        synchronized (lifecycleLock) {
            if (running) {
                logger.info("SnifferService already running");
                return;
            }
            running = true;
            startSnifferThreads();
            startWorkerThread();
            logger.info("SnifferService started.");
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        synchronized (lifecycleLock) {
            if (!running) {
                logger.info("SnifferService already stopped");
                return;
            }
            running = false;

            // close all handles
            synchronized (handles) {
                for (PcapHandle h : handles) {
                    if (h != null && h.isOpen()) {
                        try {
                            h.breakLoop();
                            h.close();
                        } catch (Exception ignored) {}
                    }
                }
                handles.clear();
            }

            snifferExecutor.shutdownNow();
            logger.info("SnifferService stopped.");
        }
    }

    @PreDestroy
    public void onShutdown() {
        stop();
    }

    // ===== Sniffer =====
    private void startSnifferThreads() {
        snifferExecutor.submit(() -> {
            try {
                List<PcapNetworkInterface> ifaces = Pcaps.findAllDevs();
                if (ifaces == null || ifaces.isEmpty()) {
                    logger.error("No network interfaces found");
                    return;
                }

                for (PcapNetworkInterface nif : ifaces) {
                    if (!nif.isUp() || nif.isLoopBack()) {
                        logger.info("Skipping interface: {}", nif.getName());
                        continue;
                    }

                    logger.info("✅ Starting sniffer on interface: {} ({})",
                            nif.getName(), nif.getDescription());

                    snifferExecutor.submit(() -> startCaptureOnInterface(nif));
                }

            } catch (Exception e) {
                logger.error("Sniffer setup error", e);
            }
        });
    }

    private void startCaptureOnInterface(PcapNetworkInterface nif) {
        try {
            int snapLen = 65536;
            PromiscuousMode mode = PromiscuousMode.PROMISCUOUS;
            int timeout = 10; // ms

            PcapHandle localHandle = nif.openLive(snapLen, mode, timeout);

            synchronized (handles) {
                handles.add(localHandle);
            }

            PacketListener listener = pkt -> {
                try {
                    PacketData dto = toDto(pkt);
                    logToFile("[" + nif.getName() + "] Captured packet → " + dto);
                    queue.offer(dto);
                    captured.add(dto);
                } catch (Exception e) {
                    logToFile("[" + nif.getName() + "] Error handling packet: " + e.getMessage());
                }
            };

            localHandle.loop(-1, listener);

        } catch (Exception e) {
            logger.error("Sniffer error on {}: {}", nif.getName(), e.getMessage(), e);
        }
    }

    // ===== Worker =====
    @Async("snifferExecutor")
    private void startWorkerThread() {
        Thread worker = new Thread(() -> {
            while (running) {
                try {
                    PacketData p = queue.poll(200, TimeUnit.MILLISECONDS);
                    if (p != null) {
                        producerClient.send(p)
                                .doOnError(e -> logToFile("Failed to send packet: " + e.getMessage()))
                                .doOnSuccess(v -> logToFile("Sent packet → " + p))
                                .subscribe();
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                } catch (Exception ex) {
                    logToFile("Worker error: " + ex.getMessage());
                }
            }
        }, "pcap-worker-thread");
        worker.setDaemon(true);
        worker.start();
    }

    // ===== DTO Conversion =====
    private PacketData toDto(Packet pkt) {
        PacketData d = new PacketData();
        d.setTimestamp(LocalDateTime.now());
        List<String> layers = new ArrayList<>();
        d.setRawLayers(layers);
        d.setProtocol("OTHER");

        if (pkt.contains(EthernetPacket.class)) {
            layers.add("Ether");
        }

        if (pkt.contains(IpV4Packet.class)) {
            IpV4Packet ip = pkt.get(IpV4Packet.class);
            layers.add("IP");
            d.setSrcIp(ip.getHeader().getSrcAddr().getHostAddress());
            d.setDstIp(ip.getHeader().getDstAddr().getHostAddress());

            IpNumber proto = ip.getHeader().getProtocol();
            if (proto.equals(IpNumber.TCP) && pkt.contains(TcpPacket.class)) {
                TcpPacket tcp = pkt.get(TcpPacket.class);
                layers.add("TCP");
                d.setSrcPort(tcp.getHeader().getSrcPort().valueAsInt());
                d.setDstPort(tcp.getHeader().getDstPort().valueAsInt());
                d.setProtocol("TCP");
                /*if (tcp.getPayload() != null) {
                    d.setPayload(extractPayloadHex(tcp.getPayload()));
                }*/
            } else if (proto.equals(IpNumber.UDP) && pkt.contains(UdpPacket.class)) {
                UdpPacket udp = pkt.get(UdpPacket.class);
                layers.add("UDP");
                d.setSrcPort(udp.getHeader().getSrcPort().valueAsInt());
                d.setDstPort(udp.getHeader().getDstPort().valueAsInt());
                d.setProtocol("UDP");
                /*if (udp.getPayload() != null) {
                    d.setPayload(extractPayloadHex(udp.getPayload()));
                }*/
            } else {
                d.setProtocol(proto.name());
            }
        } else if (pkt.contains(IpV6Packet.class)) {
            layers.add("IPV6");
            // IPv6 parsing can be added here
        }

        try {
            d.setLength(pkt.length());
        } catch (Exception e) {
            d.setLength(null);
        }

        return d;
    }

    private String extractPayload(Packet payload) {
        try {
            byte[] raw = payload.getRawData();
            // Decode safely, strip control chars
            return new String(raw, StandardCharsets.UTF_8)
                    .replaceAll("\\p{C}", "")
                    .trim();
        } catch (Exception e) {
            return null;
        }
    }

    // ===== Logging =====
    private void logToFile(String msg) {
        try (FileWriter fw = new FileWriter(props.getLogFile(), true)) {
            fw.write(LocalDateTime.now() + " | " + msg + System.lineSeparator());
        } catch (IOException e) {
            logger.warn("Unable to write log: {}", e.getMessage());
        }
    }
    

    private String extractPayloadHex(Packet payload) {
        try {
            byte[] raw = payload.getRawData();
            return Base64.getEncoder().encodeToString(raw); // always safe
        } catch (Exception e) {
            return null;
        }
    }

    
    public int getCapturedCount() {
    	System.out.println("captured:"+captured);
        return captured.size();
    }
    
    public List<PacketData> getCapturedPackets() {
        return captured;
    }
}
