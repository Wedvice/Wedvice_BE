package com.wedvice;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SecurityScheme(
    name = "JWT", // 아래 @SecurityRequirement에서 사용하는 이름
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT 인증 방식 (Bearer {token})"
)
@SpringBootApplication
@EnableScheduling
public class WedviceApplication {

  public static void main(String[] args) {
    SpringApplication.run(WedviceApplication.class, args);
  }
}
