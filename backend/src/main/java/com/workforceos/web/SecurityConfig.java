package com.workforceos.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Minimal security wiring for the development phase.
 *
 * <p>All requests are permitted while the {@link TenantContextFilter} binds the demo
 * tenant context. Production hardens this with an OIDC/OAuth 2.0 resource server and
 * use-case-level authorization.</p>
 */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, TenantContextFilter tenantContextFilter)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(tenantContextFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
