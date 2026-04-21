package com.merim.digitalpayment.underflow.sample.openapi;

import com.merim.digitalpayment.underflow.openapi.filters.JandexAwareOASFilter;
import com.merim.digitalpayment.underflow.openapi.filters.OperationDetail;
import com.merim.digitalpayment.underflow.sample.security.MySecurityScope;
import com.merim.digitalpayment.underflow.sample.security.Security;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.security.SecurityRequirement;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;

/**
 * TestFilter.
 *
 * @author Pierre Adam
 * @since 26.04.09
 */
@Slf4j
public class TestFilter implements JandexAwareOASFilter {

    /**
     * The Index view.
     */
    private IndexView indexView;

    @Override
    public void register(final IndexView indexView) {
        this.indexView = indexView;
    }

    @Override
    public Operation filterOperation(final Operation operation) {
        if (this.hasSecurityScheme(operation, Security.SECURITY_REQUIREMENT)) {
            try {
                final OperationDetail operationDetail = this.extractOperationDetail(operation).resolve(this.indexView);
                final MethodInfo methodInfo = operationDetail.getMethodInfo();

                if (methodInfo.hasAnnotation(MySecurityScope.class)) {
                    final AnnotationInstance annotation = methodInfo.annotation(MySecurityScope.class);
                    final AnnotationValue value = annotation.value();

                    String description = operation.getDescription() == null ? "" : operation.getDescription();
                    description = "Your token need the access level `" + value.asString() + "` to access this endpoint\n\n" + description;
                    operation.setDescription(description);
                }
            } catch (final Exception e) {
                TestFilter.logger.error("An error occurred while resolving the operation detail", e);
            }
        }

        return operation;
    }

    /**
     * Has security scheme boolean.
     *
     * @param operation the operation
     * @param scheme    the scheme
     * @return the boolean
     */
    private boolean hasSecurityScheme(final Operation operation, final String scheme) {
        if (operation.getSecurity() != null) {
            for (final SecurityRequirement securityRequirement : operation.getSecurity()) {
                if (securityRequirement.getSchemes() != null && securityRequirement.getSchemes().containsKey(scheme)) {
                    return true;
                }
            }
        }

        return false;
    }
}
