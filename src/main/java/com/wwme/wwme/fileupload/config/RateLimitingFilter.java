package com.wwme.wwme.fileupload.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements Filter {

    private final Bucket globalBucket;
    private final ConcurrentHashMap<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

    public RateLimitingFilter() {
        // Initialize the global bucket
        Refill globalRefill = Refill.greedy(100, Duration.ofHours(1)); // 1000 requests per hour globally
        Bandwidth globalLimit = Bandwidth.classic(100, globalRefill);
        this.globalBucket = Bucket.builder().addLimit(globalLimit).build();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization necessary
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String ip = httpRequest.getRemoteAddr();

        Bucket ipBucket = ipBuckets.computeIfAbsent(ip, this::newIpBucket);

        // Check both global and IP-specific buckets
        if (globalBucket.tryConsume(1) && ipBucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.getWriter().write("Too many requests");
            httpResponse.setStatus(429); // HTTP status code 429: Too Many Requests
        }
    }

    @Override
    public void destroy() {
        // No cleanup necessary
    }

    private Bucket newIpBucket(String ip) {
        Refill refill = Refill.greedy(10, Duration.ofMinutes(1)); // 10 requests per minute per IP
        Bandwidth limit = Bandwidth.classic(10, refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
