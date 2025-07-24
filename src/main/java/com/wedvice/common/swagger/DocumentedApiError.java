package com.wedvice.common.swagger;

import com.wedvice.common.exception.CustomException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DocumentedApiErrors.class)
public @interface DocumentedApiError {

    Class<? extends CustomException> value(); // 예외 클래스
}