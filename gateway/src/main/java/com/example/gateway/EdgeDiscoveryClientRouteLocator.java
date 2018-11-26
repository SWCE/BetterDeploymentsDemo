package com.example.gateway;

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
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.discovery.ServiceRouteMapper;

@Slf4j
public class EdgeDiscoveryClientRouteLocator extends DiscoveryClientRouteLocator {


    private final ConsulWithTagsDiscoveryClient discoveryClient;
    private final ZuulProperties zuulProperties;
    private final Set<String> ignoredServices;

    public EdgeDiscoveryClientRouteLocator(String servletPath, ZuulProperties zuulProperties,
        ServiceRouteMapper serviceRouteMapper, ConsulWithTagsDiscoveryClient discoveryClient,
        Optional<Registration> registration) {
        super(servletPath, discoveryClient, zuulProperties, serviceRouteMapper, registration.orElse(null));
        this.discoveryClient = discoveryClient;
        this.zuulProperties = zuulProperties;
        this.ignoredServices = ImmutableSet.copyOf(zuulProperties.getIgnoredServices());
    }

    @Override
    protected LinkedHashMap<String, ZuulRoute> locateRoutes() {
        zuulProperties.setIgnoredServices(Sets.newHashSet(ignoredServices));
        zuulProperties.getIgnoredServices().addAll(getExcludedServices().keySet());
        log.info("Ignored services are: {}", zuulProperties.getIgnoredServices());
        return super.locateRoutes();
    }

    private Map<String, List<String>> getExcludedServices() {
        return discoveryClient.getConsulServices().entrySet().stream()
            .filter(service -> !isEdgeService(service.getValue()))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private static boolean isEdgeService(Collection<String> tags) {
        return tags.contains("EdgeService");
    }

}
