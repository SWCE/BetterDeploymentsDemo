package com.example.edge.ribbon;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class TagRoundRobinRule extends ZoneAvoidanceRule {


    public TagRoundRobinRule() {
        super();
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        AbstractServerPredicate predicate = super.getPredicate();
        String requestTag = getAttribute();
        CompositePredicate.withPredicate(predicate);
        return StringUtils.isNotEmpty(requestTag) ? CompositePredicate
            .withPredicates(predicate, new TagBasedPredicate(requestTag)).build() : predicate;
    }

    public static String getAttribute() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            String tag = (String) requestAttributes.getAttribute("x-tag", RequestAttributes.SCOPE_REQUEST);
            if (StringUtils.isEmpty(tag) && requestAttributes instanceof ServletRequestAttributes) {
                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
                return servletRequestAttributes.getRequest().getHeader("x-tag");
            }
        }
        return null;
    }
}
