package com.merim.digitalpayment.underflow.openapi.filters;

import io.smallrye.openapi.model.Extensions;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.jboss.jandex.IndexView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ServerAwareOASFilter.
 *
 * @author Pierre Adam
 * @since 24.06.11
 */
public interface JandexAwareOASFilter extends RegistrableOASFilter<IndexView> {

    /**
     * The constant METHOD_REF_PATTERN.
     */
    Pattern METHOD_REF_PATTERN = Pattern.compile("^m(?<classHashCode>[-]?\\d+)_(?<methodHashCode>[-]?\\d+)$");

    /**
     * The constant PARAMETER_REF_PATTERN.
     */
    Pattern PARAMETER_REF_PATTERN = Pattern.compile("^p(?<classHashCode>[-]?\\d+)_(?<methodHashCode>[-]?\\d+)_(?<parameterPosition>[-]?\\d+)$");

    /**
     * Extract operation detail operation detail.
     *
     * @param operation the operation
     * @return the operation detail
     */
    default OperationDetail extractOperationDetail(final Operation operation) {
        final String methodRef = Extensions.getMethodRef(operation);
        final Matcher matcher = JandexAwareOASFilter.METHOD_REF_PATTERN.matcher(methodRef);

        if (!matcher.matches()) {
            throw new IllegalStateException("Invalid method reference");
        }

        return new OperationDetail(Integer.parseInt(matcher.group("classHashCode")),
                Integer.parseInt(matcher.group("methodHashCode")));
    }

    /**
     * Extract parameter detail parameter detail.
     *
     * @param parameter the parameter
     * @return the parameter detail
     */
    default ParameterDetail extractParameterDetail(final Parameter parameter) {
        final Matcher matcher = JandexAwareOASFilter.PARAMETER_REF_PATTERN.matcher(parameter.getRef());

        if (!matcher.matches()) {
            throw new IllegalStateException("Invalid method reference");
        }

        return new ParameterDetail(Integer.parseInt(matcher.group("classHashCode")),
                Integer.parseInt(matcher.group("methodHashCode")),
                Integer.parseInt(matcher.group("parameterPosition")));
    }
}
