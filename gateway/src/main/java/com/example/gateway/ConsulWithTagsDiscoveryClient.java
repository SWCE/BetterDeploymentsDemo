package com.example.gateway;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClientConfiguration.LifecycleRegistrationResolver;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;

@Slf4j
public class ConsulWithTagsDiscoveryClient extends ConsulDiscoveryClient {

    private final ConsulClient consulClient;

    public ConsulWithTagsDiscoveryClient(ConsulClient client, ConsulDiscoveryProperties properties,
        LifecycleRegistrationResolver lifecycleRegistrationResolver) {
        super(client, properties,  lifecycleRegistrationResolver);
        consulClient = client;
    }


    public Map<String, List<String>> getConsulServices() {
        return consulClient.getCatalogServices(QueryParams.DEFAULT).getValue();
    }

}
