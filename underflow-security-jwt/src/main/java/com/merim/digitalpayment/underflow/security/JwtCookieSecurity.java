package com.merim.digitalpayment.underflow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.undertow.server.handlers.Cookie;
import lombok.NonNull;

import javax.crypto.SecretKey;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.Optional;

/**
 * JwtCookieSecurity.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 * @author Pierre Adam
 * @since 26.02.17
 */
public abstract class JwtCookieSecurity<T, U extends Annotation> extends CookieSecurity<T, U> {

    /**
     * The Issuer.
     */
    private final String issuer;

    /**
     * The Secret key.
     */
    private final SecretKey secretKey;

    /**
     * The Refresh validity.
     */
    private final boolean refreshValidity;

    /**
     * The Object mapper.
     */
    private final ObjectMapper objectMapper;

    /**
     * The Jwt parser.
     */
    private final JwtParser jwtParser;

    /**
     * Instantiates a new Cookie security.
     *
     * @param userRepresentationClass the user representation class
     * @param scopeClass              the scope class
     * @param cookieName              the cookie name
     * @param issuer                  the issuer
     * @param secretKey               the secret key
     * @param refreshValidity         the refresh validity
     * @param objectMapper            the object mapper
     */
    public JwtCookieSecurity(@NonNull final Class<T> userRepresentationClass,
                             @NonNull final Class<U> scopeClass,
                             @NonNull final String cookieName,
                             @NonNull final String issuer,
                             @NonNull final SecretKey secretKey,
                             @NonNull final boolean refreshValidity,
                             @NonNull final ObjectMapper objectMapper) {
        super(cookieName, userRepresentationClass, scopeClass);
        this.issuer = issuer;
        this.secretKey = secretKey;
        this.refreshValidity = refreshValidity;
        this.objectMapper = objectMapper;
        this.jwtParser = Jwts.parser()
                .verifyWith(this.secretKey)
                .requireIssuer(this.issuer)
                .build();
    }

    /**
     * Instantiates a new Jwt cookie security.
     *
     * @param userRepresentationClass the user representation class
     * @param scopeClass              the scope class
     * @param cookieName              the cookie name
     * @param issuer                  the issuer
     * @param secretKey               the secret key
     * @param refreshValidity         the refresh validity
     */
    public JwtCookieSecurity(@NonNull final Class<T> userRepresentationClass,
                             @NonNull final Class<U> scopeClass,
                             @NonNull final String cookieName,
                             @NonNull final String issuer,
                             @NonNull final SecretKey secretKey,
                             @NonNull final boolean refreshValidity) {
        this(userRepresentationClass, scopeClass, cookieName, issuer, secretKey, refreshValidity, new ObjectMapper());
    }

    /**
     * Instantiates a new Jwt cookie security.
     *
     * @param userRepresentationClass the user representation class
     * @param scopeClass              the scope class
     * @param cookieName              the cookie name
     * @param issuer                  the issuer
     * @param secretKey               the secret key
     */
    public JwtCookieSecurity(@NonNull final Class<T> userRepresentationClass,
                             @NonNull final Class<U> scopeClass,
                             @NonNull final String cookieName,
                             @NonNull final String issuer,
                             @NonNull final SecretKey secretKey) {
        this(userRepresentationClass, scopeClass, cookieName, issuer, secretKey, true);
    }

    @Override
    protected final Cookie serialize(final T userRepresentation, final Cookie baseCookie) {
        try {
            final String jwt = Jwts.builder()
                    .issuer(this.issuer)
                    .issuedAt(new Date())
                    .expiration(this.getExpirationDate(userRepresentation))
                    .claim("data", userRepresentation)
                    .signWith(this.secretKey)
                    .compact();

            return baseCookie.setValue(jwt);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected final T deserialize(final Cookie cookie) throws Exception {
        final Object data = this.jwtParser
                .parseSignedClaims(cookie.getValue())
                .getPayload()
                .get("data");

        return this.objectMapper.readValue(this.objectMapper.writeValueAsString(data), this.userRepresentationClass());
    }

    @Override
    protected final Optional<Cookie> updateCookie(@NonNull final T userRepresentation, @NonNull final Cookie sessionCookie) {
        if (this.refreshValidity) {
            final Date expirationDate = this.jwtParser.parseSignedClaims(sessionCookie.getValue())
                    .getPayload()
                    .get("exp", Date.class);
            final Date newExpirationDate = this.getExpirationDate(userRepresentation);
            final long timeDifference = newExpirationDate.getTime() - expirationDate.getTime();

            if (timeDifference > 3600000L) {
                return Optional.of(this.serialize(userRepresentation, sessionCookie));
            }
        }

        return Optional.empty();
    }

    /**
     * Gets expiration date.
     *
     * @param userRepresentation the user representation
     * @return the expiration date
     */
    protected final Date getExpirationDate(final T userRepresentation) {
        return new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30);
    }
}
