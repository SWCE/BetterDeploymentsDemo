package com.example.gateway;

import com.ecwid.consul.v1.ConsulClient;
import com.example.gateway.GatewayConfiguration.DefaultRibbonConfig;
import com.example.gateway.ribbon.TagRoundRobinRule;
import com.netflix.loadbalancer.IRule;
import java.util.Optional;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClientConfiguration;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.ServiceRouteMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@RibbonClients(defaultConfiguration = DefaultRibbonConfig.class)
public class GatewayConfiguration {

    @Bean
    @ConditionalOnConsulEnabled
    public EdgeDiscoveryClientRouteLocator edgeDiscoveryClientRouteLocator(ServerProperties serverProperties,
        ZuulProperties zuulProperties, ServiceRouteMapper serviceRouteMapper, Optional<Registration> registration,
        ConsulWithTagsDiscoveryClient discoveryClient) {
        return new EdgeDiscoveryClientRouteLocator(serverProperties.getServletPath(), zuulProperties,
            serviceRouteMapper, discoveryClient, registration);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ConsulWithTagsDiscoveryClient consulWithTagsDiscoveryClient(ConsulClient consulClient,
        ConsulDiscoveryProperties consulDiscoveryProperties, ApplicationContext ctx) {
        return new ConsulWithTagsDiscoveryClient(consulClient, consulDiscoveryProperties,
            new ConsulDiscoveryClientConfiguration().new LifecycleRegistrationResolver(ctx));
    }

    @Configuration
    class DefaultRibbonConfig {

        @Bean
        public IRule ribbonRule() {
            return new TagRoundRobinRule();
        }

    }
}
