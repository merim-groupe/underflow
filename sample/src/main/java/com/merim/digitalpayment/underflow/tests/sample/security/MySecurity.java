package com.merim.digitalpayment.underflow.tests.sample.security;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.security.JwtCookieSecurity;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * MySecurity.
 *
 * @author Pierre Adam
 * @since 22.07.20
 */
public class MySecurity extends JwtCookieSecurity<MyUserRepresentation, MySecurityScope> {

    public static final String ISSUER = "underflow-sample";

    public static final String SECRET_KEY = "your-256-bit-secret!!!!!!!!!!!!!!!!!!";

    /**
     * Instantiates a new Cookie security.
     */
    public MySecurity() {
        super(MyUserRepresentation.class, MySecurityScope.class, "UnderflowSession", MySecurity.ISSUER,
                Keys.hmacShaKeyFor(MySecurity.SECRET_KEY.getBytes(StandardCharsets.UTF_8)),
                Application.getMapper());
    }

    @Override
    public boolean isAccessible(final MyUserRepresentation userRepresentation, final MySecurityScope scope) {
        if (scope == null) {
            return true;
        }
        return userRepresentation.getData().getScopes().contains(scope.value());
    }

    @Override
    protected Optional<MyUserRepresentation> updateUser(@NonNull final MyUserRepresentation userRepresentation) {
        final Instant issuedAt = userRepresentation.getIssuedAt().toInstant();
        final Instant now = Instant.now();

        // Here we can update the expiration date of the JWT which will automatically update the user cookie.
        // If the user is still logged in and still active, we can extend the session duration.
        //
        // You can implement all sort of custom logic to update the user representation here.
        // Keep in mind that this function is called every time a user reaches a secured endpoint.
        // As such, you should make it as lightweight as possible but as complex as necessary.
        if (Duration.between(issuedAt, now).toMinutes() >= 2) {
            userRepresentation.setExpiration(Date.from(Instant.now().plus(Duration.ofHours(1))));
            return Optional.of(userRepresentation);
        }

        return Optional.empty();
    }
}
