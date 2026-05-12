package com.marjane.ems.Threading;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestAuditFilter extends OncePerRequestFilter {

    private final AsyncAuditLogger auditLogger;

    public RequestAuditFilter(AsyncAuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        filterChain.doFilter(request, response);

        String method = request.getMethod();
        if (!"POST".equalsIgnoreCase(method) && !"PUT".equalsIgnoreCase(method)) {
            return;
        }

        String path = request.getRequestURI();
        String eid = resolveEidFromSecurityContext();
        int statusCode = response.getStatus();

        auditLogger.logRequestEvent(eid, method, path, statusCode);
    }

    private String resolveEidFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "UNKNOWN";
        }

        Object principal = authentication.getPrincipal();
        return principal instanceof String ? (String) principal : "UNKNOWN";
    }
}
