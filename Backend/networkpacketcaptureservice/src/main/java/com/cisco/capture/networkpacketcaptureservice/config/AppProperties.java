package com.cisco.capture.networkpacketcaptureservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String producerUrl;
    private String captureInterface;
    private String logFile;

    public String getProducerUrl() { return producerUrl; }
    public void setProducerUrl(String producerUrl) { this.producerUrl = producerUrl; }

    public String getCaptureInterface() { return captureInterface; }
    public void setCaptureInterface(String captureInterface) { this.captureInterface = captureInterface; }

    public String getLogFile() { return logFile; }
    public void setLogFile(String logFile) { this.logFile = logFile; }
	@Override
	public String toString() {
		return "AppProperties [producerUrl=" + producerUrl + ", captureInterface=" + captureInterface + ", logFile="
				+ logFile + "]";
	}
    
    
}