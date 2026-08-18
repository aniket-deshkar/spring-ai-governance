package io.github.aniketdeshkar.governance;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public record ToolDescriptor(
    String name,
    RiskLevel risk,
    List<String> requiredAuthorities,
    List<ArgumentRule> argumentRules) {
  public ToolDescriptor {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
    risk = Objects.requireNonNull(risk, "risk");
    requiredAuthorities = List.copyOf(requiredAuthorities);
    argumentRules = List.copyOf(argumentRules);
  }

  public static ToolDescriptor unrestricted(String name) {
    return new ToolDescriptor(name, RiskLevel.LOW, List.of(), List.of());
  }

  public static ToolDescriptor from(Method method) {
    GovernedTool annotation = method.getAnnotation(GovernedTool.class);
    if (annotation == null) {
      throw new IllegalArgumentException("method is not annotated with @GovernedTool");
    }
    String name = annotation.value().isBlank() ? method.getName() : annotation.value();
    return new ToolDescriptor(
        name, annotation.risk(), List.of(annotation.authorities()), List.of());
  }

  public ToolDescriptor withArgumentRules(ArgumentRule... rules) {
    return new ToolDescriptor(name, risk, requiredAuthorities, List.of(rules));
  }
}
