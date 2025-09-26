package com.cisco.capture.networkpacketcaptureservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NetworkpacketcaptureserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetworkpacketcaptureserviceApplication.class, args);
	}

}
