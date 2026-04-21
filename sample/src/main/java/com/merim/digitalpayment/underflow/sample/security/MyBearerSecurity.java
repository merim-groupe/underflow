package com.merim.digitalpayment.underflow.sample.security;

import com.merim.digitalpayment.underflow.security.HeaderSecurity;

import java.util.Optional;

/**
 * BearerSecurity.
 *
 * @author Pierre Adam
 * @since 22.07.19
 */
public class MyBearerSecurity extends HeaderSecurity<MyBearerUserRepresentation, MySecurityScope> {

    /**
     * Instantiates a new My bearer security.
     */
    public MyBearerSecurity() {
        super(MyBearerUserRepresentation.class, MySecurityScope.class);
    }

    @Override
    protected Optional<MyBearerUserRepresentation> isLogged(final String value) {
        if (value.length() > 3) {
            return Optional.of(new MyBearerUserRepresentation(value));
        }
        return Optional.empty();
    }

    @Override
    public boolean isAccessible(final MyBearerUserRepresentation userRepresentation, final MySecurityScope scope) {
        return true;
    }

    @Override
    public Class<MySecurityScope> scopeAnnotationClass() {
        return MySecurityScope.class;
    }
}
