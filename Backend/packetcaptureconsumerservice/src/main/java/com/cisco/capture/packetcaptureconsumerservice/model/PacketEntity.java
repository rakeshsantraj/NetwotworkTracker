package com.cisco.capture.packetcaptureconsumerservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="packets")
public class PacketEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String srcIp;
    private String dstIp;
    private String protocol;
    private Integer srcPort;
    private Integer dstPort;
    private Integer length;
    @Column(name="payload", length=2000)
    private String payload;
    private LocalDateTime timestamp;
	public PacketEntity(String srcIp, String dstIp, String protocol, Integer srcPort, Integer dstPort, Integer length,
			String payload, LocalDateTime timestamp) {
		super();
		this.srcIp = srcIp;
		this.dstIp = dstIp;
		this.protocol = protocol;
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.length = length;
		this.payload = payload;
		this.timestamp = timestamp;
	}
	public PacketEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getSrcIp() {
		return srcIp;
	}
	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}
	public String getDstIp() {
		return dstIp;
	}
	public void setDstIp(String dstIp) {
		this.dstIp = dstIp;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public Integer getSrcPort() {
		return srcPort;
	}
	public void setSrcPort(Integer srcPort) {
		this.srcPort = srcPort;
	}
	public Integer getDstPort() {
		return dstPort;
	}
	public void setDstPort(Integer dstPort) {
		this.dstPort = dstPort;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "PacketEntity [id=" + id + ", srcIp=" + srcIp + ", dstIp=" + dstIp + ", protocol=" + protocol
				+ ", srcPort=" + srcPort + ", dstPort=" + dstPort + ", length=" + length + ", payload=" + payload
				+ ", timestamp=" + timestamp + "]";
	}
    
    
}