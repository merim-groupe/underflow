package com.merimdigitalmedia.underflow.tests.sample.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merimdigitalmedia.underflow.security.CookieSecurity;
import io.undertow.server.handlers.Cookie;

/**
 * MyCookieSecurity.
 *
 * @author Pierre Adam
 * @since 22.07.20
 */
public class MyCookieSecurity extends CookieSecurity<MyUserRepresentation, MySecurityScope> {

    /**
     * Instantiates a new Cookie security.
     */
    public MyCookieSecurity() {
        super("UnderflowSession", MyUserRepresentation.class, MySecurityScope.class);
    }

    @Override
    public boolean isAccessible(final MyUserRepresentation userRepresentation, final MySecurityScope scope) {
        if (scope == null) {
            return true;
        }
        return userRepresentation.getScopes().contains(scope.value());
    }

    @Override
    protected Cookie serialize(final MyUserRepresentation userRepresentation, final Cookie cookie) {
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            cookie.setValue(objectMapper.writeValueAsString(userRepresentation));
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return cookie;
    }

    @Override
    protected MyUserRepresentation deserialize(final Cookie cookie) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(cookie.getValue(), MyUserRepresentation.class);
    }
}
