package com.merimdigitalmedia.underflow.tests.security;

import com.merimdigitalmedia.underflow.security.HeaderSecurity;

import java.util.Optional;

/**
 * BearerSecurity.
 *
 * @author Pierre Adam
 * @since 22.07.19
 */
public class MyBearerSecurity extends HeaderSecurity<MyUserRepresentation, MySecurityScope> {

    /**
     * Instantiates a new My bearer security.
     */
    public MyBearerSecurity() {
        super(MyUserRepresentation.class, MySecurityScope.class);
    }

    @Override
    protected Optional<MyUserRepresentation> isLogged(final String value) {
        if (value.length() > 3) {
            return Optional.of(new MyUserRepresentation(value));
        }
        return Optional.empty();
    }

    @Override
    public boolean isAccessible(final MyUserRepresentation userRepresentation, final MySecurityScope scope) {
        return true;
    }

    @Override
    public Class<MySecurityScope> scopeAnnotationClass() {
        return MySecurityScope.class;
    }
}
