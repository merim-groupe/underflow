package com.merim.digitalpayment.underflow.openapi.filters;

import lombok.Getter;

/**
 * ParameterDetail.
 *
 * @author Pierre Adam
 * @since 24.06.13
 */
@Getter
public class ParameterDetail extends OperationDetail {

    /**
     * The Parameter position.
     */
    private final int parameterPosition;

    /**
     * Instantiates a new Parameter detail.
     *
     * @param classHashCode     the class hash code
     * @param methodHashCode    the method hash code
     * @param parameterPosition the parameter position
     */
    public ParameterDetail(final int classHashCode, final int methodHashCode, final int parameterPosition) {
        super(classHashCode, methodHashCode);
        this.parameterPosition = parameterPosition;
    }
}
