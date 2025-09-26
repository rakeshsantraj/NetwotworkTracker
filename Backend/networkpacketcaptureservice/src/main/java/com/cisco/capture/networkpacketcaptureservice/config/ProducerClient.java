package com.cisco.capture.networkpacketcaptureservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.cisco.capture.networkpacketcaptureservice.dto.PacketData;

import reactor.core.publisher.Mono;

@Component
public class ProducerClient {

    private final WebClient webClient;

    public ProducerClient(AppProperties props) {
        this.webClient = WebClient.builder()
                .baseUrl(props.getProducerUrl())
                .build();
    }

    public Mono<Void> send(PacketData packet) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(packet)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> Mono.empty()); // swallow and continue
    }
    
    
}
