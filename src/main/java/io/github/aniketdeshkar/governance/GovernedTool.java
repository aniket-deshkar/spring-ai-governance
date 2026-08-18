package io.github.aniketdeshkar.governance;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GovernedTool {
  String value() default "";

  RiskLevel risk() default RiskLevel.LOW;

  String[] authorities() default {};
}
