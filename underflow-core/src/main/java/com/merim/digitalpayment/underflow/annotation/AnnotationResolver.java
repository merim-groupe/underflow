package com.merim.digitalpayment.underflow.annotation;

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
    public static <T extends Annotation> Optional<T> annotation(final AnnotatedElement annotatedElement,
                                                                final Class<T> annotationClass) {
        if (annotationClass.isAnnotationPresent(Inherited.class)) {
            return AnnotationResolver.nested(annotatedElement, annotationClass, 0);
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
    public static <T extends Annotation> Optional<T> nestedAnnotation(final AnnotatedElement annotatedElement,
                                                                      final Class<T> annotationClass) {
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
    private static <T extends Annotation> Optional<T> nested(final AnnotatedElement annotatedElement,
                                                             final Class<T> annotationClass,
                                                             final int depth) {
        if (depth > 5) { // Why 5 ? Because why not ... If you need more, you fucked up somewhere !
            // Not too deep step annotation !
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
