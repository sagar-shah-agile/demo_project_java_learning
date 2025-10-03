package com.javatraining.notification_mgmt.aspect;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

/*@Component
public class MDCFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID, traceId); // put traceId into logging context

            response.addHeader(TRACE_ID, traceId); // send back to client (optional)
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID); // cleanup to avoid memory leaks
        }

    }
}*/
