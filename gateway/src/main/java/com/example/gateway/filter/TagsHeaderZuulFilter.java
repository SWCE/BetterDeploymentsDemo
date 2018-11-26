package com.example.gateway.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagsHeaderZuulFilter extends ZuulFilter {

    public static final int TAGS_HEADER_FILTER_ORDER = FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;

    public static final String TAG_HEADER = "x-tag";

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return TAGS_HEADER_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        return currentContext.get(SERVICE_ID_KEY) != null;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        String headerTag = requestContext.getRequest().getHeader(TAG_HEADER);
        if (!StringUtils.isEmpty(headerTag)) {
            log.info("User requested tag {}", headerTag);

            // Pass the data to called service
            requestContext.addZuulRequestHeader(TAG_HEADER, headerTag);
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            requestAttributes.setAttribute(TAG_HEADER, headerTag, RequestAttributes.SCOPE_REQUEST);
        }
        return null;
    }
}
