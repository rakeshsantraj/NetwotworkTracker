package com.cisco.capture.packetcaptureconsumerservice.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PacketData {

    private String srcIp;
    private String dstIp;
    private String protocol;
    private Integer srcPort;
    private Integer dstPort;
    private Integer length;
    private String payload;
    //private List<String> raw_layers;  // or maybe a List<String> depending on your producer

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
	public PacketData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PacketData(String srcIp, String dstIp, String protocol, Integer srcPort, Integer dstPort, Integer length,
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
		//this.raw_layers= raw_layers;
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
	/*public List<String> getRawLayers() {
		return raw_layers;
	}
	public void setRawLayers(List<String> raw_layers) {
		this.raw_layers = raw_layers;
	}*/
	@Override
	public String toString() {
		return "PacketData [srcIp=" + srcIp + ", dstIp=" + dstIp + ", protocol=" + protocol + ", srcPort=" + srcPort
				+ ", dstPort=" + dstPort + ", length=" + length + ", payload=" + payload + ", timestamp=" + timestamp + "]";
	}
}
