package com.example.edge;

import com.example.edge.EdgeConfiguration.DefaultRibbonConfig;
import com.example.edge.ribbon.TagRoundRobinRule;
import com.netflix.loadbalancer.IRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistrationCustomizer;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
@RibbonClients(defaultConfiguration = DefaultRibbonConfig.class)
public class EdgeConfiguration {

    @Bean
    public ConsulRegistrationCustomizer consulRegistrationCustomizer() {
        log.info("Adding EdgeService tag");
        return registration -> registration.getService().getTags().add("EdgeService");
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    public RestTemplateCustomizer tagsClientHttpRequestInterceptorCustomizer() {
        return restTemplate -> restTemplate.getInterceptors().add(tagClientHttpRequestInterceptor());
    }

    private ClientHttpRequestInterceptor tagClientHttpRequestInterceptor() {
        return (request, body, execution) -> {
            String tag = TagRoundRobinRule.getAttribute();
            if (!StringUtils.isEmpty(tag)) {
                log.info("Passing x-tag value to request: {}", tag);
                request.getHeaders().add("x-tag", tag);
            }
            return execution.execute(request, body);
        };
    }

    @Configuration
    public class DefaultRibbonConfig {

        @Bean
        public IRule ribbonRule() {
            return new TagRoundRobinRule();
        }

    }
}
