package com.example.edge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EdgeController {

    private final ConsulDiscoveryProperties consulDiscoveryProperties;

    @LoadBalanced
    private final RestTemplate restTemplate;

    @GetMapping("/api/hello")
    public String hello() {
        log.info("Edge service got request");
        String res = restTemplate.getForObject("http://backend/api/backend", String.class);
        log.info("Result from backend {}", res);
        return ("Hello " + consulDiscoveryProperties.getTags()) + "\n" + res;
    }

}
