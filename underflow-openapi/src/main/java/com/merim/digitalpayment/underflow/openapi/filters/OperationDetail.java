package com.merim.digitalpayment.underflow.openapi.filters;

import lombok.Getter;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;

/**
 * OperationDetail.
 *
 * @author Pierre Adam
 * @since 24.06.13
 */
@Getter
public class OperationDetail {

    /**
     * The Class hash code.
     */
    private final int classHashCode;

    /**
     * The Method hash code.
     */
    private final int methodHashCode;

    /**
     * The Class info.
     */
    private ClassInfo classInfo;

    /**
     * The Method info.
     */
    private MethodInfo methodInfo;

    /**
     * Instantiates a new Operation detail.
     *
     * @param classHashCode  the class hash code
     * @param methodHashCode the method hash code
     */
    public OperationDetail(final int classHashCode, final int methodHashCode) {
        this.classHashCode = classHashCode;
        this.methodHashCode = methodHashCode;
    }

    /**
     * Resolve operation detail.
     *
     * @param indexView the index view
     * @return the operation detail
     */
    public OperationDetail resolve(final IndexView indexView) {
        for (final ClassInfo knownClass : indexView.getKnownClasses()) {
            if (knownClass.hashCode() == this.classHashCode) {
                this.classInfo = knownClass;

                for (final MethodInfo method : knownClass.methods()) {
                    if (method.hashCode() == this.methodHashCode) {
                        this.methodInfo = method;
                        return this;
                    }
                }

                throw new IllegalStateException("Method " + this.methodHashCode + " not found");
            }
        }

        throw new IllegalStateException("Class " + this.classHashCode + " not found");
    }
}
