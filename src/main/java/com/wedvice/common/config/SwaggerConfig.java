package com.wedvice.common.config;

import com.wedvice.common.exception.CustomException;
import com.wedvice.common.swagger.DocumentedApiError;
import com.wedvice.common.swagger.DocumentedApiErrors;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.springdoc.core.customizers.OperationCustomizer;
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
                .license(new License().name("Apache 2.0").url("http://springdoc.org")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("개발용 서버"),
                new Server()
                    .url("https://wedy.co.kr")
                    .description("배포용 서버")
            ));
    }

    @Bean
    public OperationCustomizer documentedApiErrorCustomizer() {
        return (operation, handlerMethod) -> {
            Method method = handlerMethod.getMethod();
            List<Class<? extends CustomException>> exceptionList = new ArrayList<>();

            if (method.isAnnotationPresent(DocumentedApiError.class)) {
                exceptionList.add(method.getAnnotation(DocumentedApiError.class).value());
            }
            if (method.isAnnotationPresent(DocumentedApiErrors.class)) {
                for (DocumentedApiError ann : method.getAnnotation(DocumentedApiErrors.class)
                    .value()) {
                    exceptionList.add(ann.value());
                }
            }

            for (Class<? extends CustomException> exClass : exceptionList) {
                try {
                    CustomException ex = exClass.getDeclaredConstructor().newInstance();
                    String status = String.valueOf(ex.getHttpStatus().value());

                    MediaType mediaType = new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/ApiErrorResponse"))
                        .example(ex.toExampleJson());

                    ApiResponse response = new ApiResponse()
                        .description(ex.getMessage())
                        .content(new io.swagger.v3.oas.models.media.Content()
                            .addMediaType("application/json", mediaType));

                    operation.getResponses().addApiResponse(status, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return operation;
        };
    }
}
