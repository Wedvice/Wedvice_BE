package com.wedvice.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfiguration {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(
//                "hyunggeun"
//                서브태스크 작성할 때 작동하는지 확인
                Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                        .filter(authentication -> authentication.isAuthenticated())
                        .map(org.springframework.security.core.Authentication::getName)
                        .orElse(null)

        );
    }
}
