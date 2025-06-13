package com.wedvice.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("💍 Wedvice 결혼 서비스 API 문서")
                        .description("👩‍❤️‍👨 Wedvice 결혼 서비스의 API 명세서입니다.")
                        .version("v1.0.0")
                        .contact(new Contact().name("Wedvice 팀").email("support@wedvice.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
