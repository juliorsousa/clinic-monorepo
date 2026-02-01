package com.ifba.clinic.access.security.annotations;

import com.ifba.clinic.access.security.enums.TraitPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(
    "@userGrantsEvaluator.checkTrait(#root.methodAnnotation.value(), #root.methodAnnotation.policy())"
)
public @interface TraitRestricted {
  String value();
  TraitPolicy policy() default TraitPolicy.HAVE;
}
