package com.merim.digitalpayment.underflow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
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
public abstract class JwtCookieSecurity<T extends JwtUserRepresentation<?>, U extends Annotation> extends CookieSecurity<T, U> {

    /**
     * The Issuer.
     */
    private final String issuer;

    /**
     * The Secret key.
     */
    private final SecretKey secretKey;

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
     * @param objectMapper            the object mapper
     */
    public JwtCookieSecurity(@NonNull final Class<T> userRepresentationClass,
                             @NonNull final Class<U> scopeClass,
                             @NonNull final String cookieName,
                             @NonNull final String issuer,
                             @NonNull final SecretKey secretKey,
                             @NonNull final ObjectMapper objectMapper) {
        super(cookieName, userRepresentationClass, scopeClass);
        this.issuer = issuer;
        this.secretKey = secretKey;
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
     */
    public JwtCookieSecurity(@NonNull final Class<T> userRepresentationClass,
                             @NonNull final Class<U> scopeClass,
                             @NonNull final String cookieName,
                             @NonNull final String issuer,
                             @NonNull final SecretKey secretKey) {
        this(userRepresentationClass, scopeClass, cookieName, issuer, secretKey, new ObjectMapper());
    }

    @Override
    protected final Cookie serialize(final T userRepresentation, final Cookie baseCookie) {
        try {
            final JwtBuilder jwtBuilder = Jwts.builder()
                    .issuer(this.issuer)
                    .issuedAt(new Date());

            if (userRepresentation.getSubject() != null) {
                jwtBuilder.subject(userRepresentation.getSubject());
            }
            if (userRepresentation.getAudience() != null) {
                userRepresentation.getAudience().forEach(jwtBuilder.audience()::add);
            }
            if (userRepresentation.getExpiration() != null) {
                jwtBuilder.expiration(userRepresentation.getExpiration());
            }
            if (userRepresentation.getNotBefore() != null) {
                jwtBuilder.notBefore(userRepresentation.getNotBefore());
            }
            if (userRepresentation.getJwtId() != null) {
                jwtBuilder.id(userRepresentation.getJwtId());
            }

            final String jwt = jwtBuilder
                    .claim("data", userRepresentation.getData())
                    .signWith(this.secretKey)
                    .compact();

            return baseCookie.setValue(jwt);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected final T deserialize(final Cookie cookie) throws Exception {
        final Claims claims = this.jwtParser
                .parseSignedClaims(cookie.getValue())
                .getPayload();

        final JwtUserRepresentation<Object> tmpUserRepresentation = new JwtUserRepresentation<>() {
        };
        tmpUserRepresentation.issuer = claims.getIssuer();
        tmpUserRepresentation.setSubject(claims.getSubject());
        tmpUserRepresentation.setAudience(claims.getAudience());
        tmpUserRepresentation.setExpiration(claims.getExpiration());
        tmpUserRepresentation.setNotBefore(claims.getNotBefore());
        tmpUserRepresentation.issuedAt = claims.getIssuedAt();
        tmpUserRepresentation.setJwtId(claims.getId());
        tmpUserRepresentation.setData(claims.get("data"));

        return this.objectMapper.readValue(this.objectMapper.writeValueAsString(tmpUserRepresentation), this.userRepresentationClass());
    }

    @Override
    protected final Optional<Cookie> updateCookie(@NonNull final T userRepresentation, @NonNull final Cookie sessionCookie) {
        return this.updateUser(userRepresentation).map(t -> this.serialize(t, sessionCookie));
    }

    /**
     * Update user.
     * Can be overridden to update the user representation when the user query a page.
     * Return Optional.empty() to not update the user representation.
     *
     * @param userRepresentation the user representation
     * @return the optional
     */
    protected Optional<T> updateUser(@NonNull final T userRepresentation) {
        return Optional.empty();
    }
}
