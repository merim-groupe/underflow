package com.merim.digitalpayment.underflow.annotation;

import org.wildfly.common.annotation.NotNull;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

/**
 * AnnotationResolution.
 *
 * @author Pierre Adam
 * @since 24.06.12
 */
public class AnnotationResolver {

    /**
     * Gets annotation and resolved it in a nested way if the annotation is explicitly annotated with @Inherited
     *
     * @param <T>              the type parameter
     * @param annotatedElement the annotated element
     * @param annotationClass  the annotation class
     * @return the annotation
     */
    public static <T extends Annotation> Optional<T> annotation(@NotNull final AnnotatedElement annotatedElement,
                                                                @NotNull final Class<T> annotationClass) {
        if (annotationClass.isAnnotationPresent(Inherited.class)) {
            return AnnotationResolver.nestedAnnotation(annotatedElement, annotationClass);
        } else {
            return Optional.ofNullable(annotatedElement.getAnnotation(annotationClass));
        }
    }

    /**
     * Gets the annotation and resolves it in a nested way whatever happens.
     *
     * @param <T>              the type parameter
     * @param annotatedElement the annotated element
     * @param annotationClass  the annotation class
     * @return the nested annotation
     */
    public static <T extends Annotation> Optional<T> nestedAnnotation(@NotNull final AnnotatedElement annotatedElement,
                                                                      @NotNull final Class<T> annotationClass) {
        return AnnotationResolver.nested(annotatedElement, annotationClass, 0);
    }

    /**
     * Resolve the annotation in a nested way.
     *
     * @param <T>              the type parameter
     * @param annotatedElement the annotated element
     * @param annotationClass  the annotation class
     * @param depth            the depth
     * @return the optional
     */
    private static <T extends Annotation> Optional<T> nested(@NotNull final AnnotatedElement annotatedElement,
                                                             @NotNull final Class<T> annotationClass,
                                                             @NotNull final int depth) {
        if (depth > 10) { // Why 10? Because why not ... If you need more, you fucked up somewhere!
            // Not too deep step annotation!
            return Optional.empty();
        }

        final T annotation = annotatedElement.getAnnotation(annotationClass);

        if (annotation == null) {
            for (final Annotation typeAnnotation : annotatedElement.getAnnotations()) {
                final Optional<T> result = AnnotationResolver.nested(typeAnnotation.annotationType(), annotationClass, depth + 1);
                if (result.isPresent()) {
                    return result;
                }
            }
        }

        return Optional.ofNullable(annotation);
    }
}
