package com.example.gateway.ribbon;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import java.util.List;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.consul.discovery.ConsulServer;

@Slf4j
public class TagBasedPredicate extends AbstractServerPredicate {

    private final String tag;

    TagBasedPredicate(String tag) {
        log.info("Creating predicate for tag {}", tag);
        this.tag = tag;
    }

    @Override
    public boolean apply(@Nullable PredicateKey input) {
        return input != null && input.getServer() instanceof ConsulServer && tagsMatch(input);
    }

    private boolean tagsMatch(PredicateKey input) {
        List<String> serverTags = ((ConsulServer) input.getServer()).getHealthService().getService().getTags();
        log.info("Service tags: {}", serverTags);
        return serverTags.contains(tag);
    }

}
