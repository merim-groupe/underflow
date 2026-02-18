package com.merim.digitalpayment.underflow.security;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

/**
 * JwtUserRepresentation.
 *
 * @param <T> the type parameter
 * @author Pierre Adam
 * @since 26.02.17
 */
@Getter
public class JwtUserRepresentation<T> {

    /**
     * The Issuer.
     */
    String issuer;
    /**
     * The Issued at.
     */
    Date issuedAt;
    /**
     * The Subject.
     */
    @Setter
    private String subject;
    /**
     * The Audience.
     */
    @Setter
    private Set<String> audience;
    /**
     * The Expiration.
     */
    @Setter
    private Date expiration;
    /**
     * The Not before.
     */
    @Setter
    private Date notBefore;
    /**
     * The Jwt id.
     */
    @Setter
    private String jwtId;

    /**
     * The Data.
     */
    @Setter
    private T data;
}
