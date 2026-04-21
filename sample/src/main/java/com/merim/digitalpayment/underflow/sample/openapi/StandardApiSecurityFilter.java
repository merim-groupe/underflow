package com.merim.digitalpayment.underflow.sample.openapi;

import com.merim.digitalpayment.underflow.openapi.filters.SecurityResponseInjectorFilter;
import com.merim.digitalpayment.underflow.sample.security.Security;
import io.undertow.util.StatusCodes;

/**
 * StandardApiSecurityFilter.
 *
 * @author Pierre Adam
 * @since 26.04.20
 */
public class StandardApiSecurityFilter extends SecurityResponseInjectorFilter {

    /**
     * Instantiates a new Device session security filter.
     */
    public StandardApiSecurityFilter() {
        super(Security.SECURITY_REQUIREMENT);

        this.addResponse(Security.SECURITY_REQUIREMENT + "UnauthorizedResponse", StatusCodes.UNAUTHORIZED,
                        SecurityResponseInjectorFilter.getStandardJsonErrorResponse("Unauthorized if the session does not exists, is invalid or has expired."))
                .addResponse(Security.SECURITY_REQUIREMENT + "ForbiddenResponse", StatusCodes.FORBIDDEN,
                        SecurityResponseInjectorFilter.getStandardJsonErrorResponse("Forbidden if the session is valid but cannot access the resource."));
    }
}
