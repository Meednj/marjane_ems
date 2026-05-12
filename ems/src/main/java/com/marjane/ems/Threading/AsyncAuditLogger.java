package com.marjane.ems.Threading;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AsyncAuditLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger("AUDIT_LOG");
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public void logRequestEvent(String eid, String method, String path, int statusCode) {
        executor.submit(() -> LOGGER.info(
            "time={} type=REQUEST method={} path={} status={} eid={}",
            LocalDateTime.now(),
            method,
            path,
            statusCode,
            sanitizeEid(eid)
        ));
    }

    public void logAuthEvent(String eid, String action, String path) {
        executor.submit(() -> LOGGER.info(
            "time={} type=AUTH action={} path={} eid={}",
            LocalDateTime.now(),
            action,
            path,
            sanitizeEid(eid)
        ));
    }

    private String sanitizeEid(String eid) {
        return (eid == null || eid.isBlank()) ? "UNKNOWN" : eid;
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}
