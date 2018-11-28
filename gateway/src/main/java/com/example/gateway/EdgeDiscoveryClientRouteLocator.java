package com.example.gateway;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.discovery.ServiceRouteMapper;

@Slf4j
public class EdgeDiscoveryClientRouteLocator extends DiscoveryClientRouteLocator {


    private final ConsulClient consulClient;
    private final ZuulProperties zuulProperties;
    private final Set<String> ignoredServices;

    public EdgeDiscoveryClientRouteLocator(String servletPath, ZuulProperties zuulProperties,
        ServiceRouteMapper serviceRouteMapper, DiscoveryClient discoveryClient, Optional<Registration> registration,
        ConsulClient consulClient) {
        super(servletPath, discoveryClient, zuulProperties, serviceRouteMapper, registration.orElse(null));
        this.consulClient = consulClient;
        this.zuulProperties = zuulProperties;
        this.ignoredServices = ImmutableSet.copyOf(zuulProperties.getIgnoredServices());
    }

    @Override
    protected LinkedHashMap<String, ZuulRoute> locateRoutes() {
        zuulProperties.setIgnoredServices(Sets.newHashSet(ignoredServices));
        zuulProperties.getIgnoredServices().addAll(getExcludedServices());
        log.info("Ignored services are: {}", zuulProperties.getIgnoredServices());
        return super.locateRoutes();
    }

    private List<String> getExcludedServices() {
        return getConsulServices().entrySet().stream().filter(service -> !isEdgeService(service.getValue()))
            .map(Entry::getKey).collect(Collectors.toList());
    }

    private static boolean isEdgeService(Collection<String> tags) {
        return tags.contains("EdgeService");
    }

    private Map<String, List<String>> getConsulServices() {
        return consulClient.getCatalogServices(QueryParams.DEFAULT).getValue();
    }
}
