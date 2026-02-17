package com.merim.digitalpayment.underflow.tests.sample.security;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.security.JwtCookieSecurity;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

/**
 * MySecurity.
 *
 * @author Pierre Adam
 * @since 22.07.20
 */
public class MySecurity extends JwtCookieSecurity<MyUserRepresentation, MySecurityScope> {

    /**
     * Instantiates a new Cookie security.
     */
    public MySecurity() {
        super(MyUserRepresentation.class, MySecurityScope.class, "UnderflowSession", "underflow-sample",
                Keys.hmacShaKeyFor("your-256-bit-secret!!!!!!!!!!!!!!!!!!".getBytes(StandardCharsets.UTF_8)),
                true, Application.getMapper());
    }

    @Override
    public boolean isAccessible(final MyUserRepresentation userRepresentation, final MySecurityScope scope) {
        if (scope == null) {
            return true;
        }
        return userRepresentation.getScopes().contains(scope.value());
    }
}
