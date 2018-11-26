package com.example.gateway.ribbon;

import com.example.gateway.filter.TagsHeaderZuulFilter;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

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

    private static String getAttribute() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return (String) requestAttributes
                .getAttribute(TagsHeaderZuulFilter.TAG_HEADER, RequestAttributes.SCOPE_REQUEST);
        }
        return null;
    }
}
