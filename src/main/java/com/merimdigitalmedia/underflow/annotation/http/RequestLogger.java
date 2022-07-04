package com.merimdigitalmedia.underflow.annotation.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RequestLogger.
 *
 * @author Pierre Adam
 * @since 22.07.04
 */
public class RequestLogger {

    private RequestLogger() {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SuppressLog {
    }
}
