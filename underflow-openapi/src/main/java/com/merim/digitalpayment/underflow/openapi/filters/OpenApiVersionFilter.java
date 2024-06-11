package com.merim.digitalpayment.underflow.openapi.filters;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.info.Info;

/**
 * OpenApiVersion.
 *
 * @author Pierre Adam
 * @since 24.06.11
 */
public class OpenApiVersionFilter implements OASFilter {

    /**
     * The Version.
     */
    protected String version;

    /**
     * Instantiates a new Open api version filter.
     *
     * @param version the version
     */
    public OpenApiVersionFilter(final String version) {
        this.version = version;
    }

    @Override
    public void filterOpenAPI(final OpenAPI openAPI) {
        if (this.version != null) {
            final Info info = openAPI.getInfo();

            if (info != null) {
                info.setVersion(this.version);
            }
        }
    }
}
