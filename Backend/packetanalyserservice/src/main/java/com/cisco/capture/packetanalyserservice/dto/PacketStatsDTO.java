package com.cisco.capture.packetanalyserservice.dto;

public class PacketStatsDTO {

	  private String time;
	    private Long packets;

	    public PacketStatsDTO(String time, Long packets) {
	        this.time = time;
	        this.packets = packets;
	    }

	    public String getTime() { return time; }
	    public void setTime(String time) { this.time = time; }

	    public Long getPackets() { return packets; }
	    public void setPackets(Long packets) { this.packets = packets; }

		@Override
		public String toString() {
			return "PacketStatsDTO [time=" + time + ", packets=" + packets + "]";
		}
}
