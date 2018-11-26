package com.example.backend;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BackendController {


    private final ConsulDiscoveryProperties consulDiscoveryProperties;

    @GetMapping("/api/backend")
    public String backend(HttpServletRequest request) {
        String tag = request.getHeader("x-tag");
        if (!StringUtils.isEmpty(tag)) {
            log.info("Requested tag is {}", tag);
        }
        log.info("Backend service got request");
        return "Backend " + consulDiscoveryProperties.getTags();
    }

}
