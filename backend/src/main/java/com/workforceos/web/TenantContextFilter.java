package com.workforceos.web;

import com.workforceos.shared.context.TenantContext;
import com.workforceos.shared.context.TenantContextHolder;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Binds a {@link TenantContext} for the duration of each request.
 *
 * <p><strong>Dev-mode only.</strong> This filter resolves a demo identity from request
 * headers ({@code X-Tenant-Id}, {@code X-User-Id}) to enable local development without an
 * OIDC provider. Production replaces this with an OAuth 2.0 resource-server that derives
 * the tenant from the authenticated claim, never from a client-supplied header.</p>
 */
@Component
public class TenantContextFilter extends OncePerRequestFilter {

    private static final TenantId DEFAULT_TENANT = TenantId.of("00000000-0000-0000-0000-000000000001");
    private static final UserId DEFAULT_USER = UserId.of("00000000-0000-0000-0000-000000000002");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        TenantContextHolder.set(new TenantContext(resolveTenant(request), resolveUser(request), Set.of()));
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }

    private TenantId resolveTenant(HttpServletRequest request) {
        String header = request.getHeader("X-Tenant-Id");
        if (header == null || header.isBlank()) {
            return DEFAULT_TENANT;
        }
        try {
            return TenantId.of(header.trim());
        } catch (IllegalArgumentException ex) {
            return DEFAULT_TENANT;
        }
    }

    private UserId resolveUser(HttpServletRequest request) {
        String header = request.getHeader("X-User-Id");
        if (header == null || header.isBlank()) {
            return DEFAULT_USER;
        }
        try {
            return UserId.of(header.trim());
        } catch (IllegalArgumentException ex) {
            return DEFAULT_USER;
        }
    }
}
