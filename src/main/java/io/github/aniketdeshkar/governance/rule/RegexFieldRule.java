package io.github.aniketdeshkar.governance.rule;

import io.github.aniketdeshkar.governance.ArgumentRule;
import io.github.aniketdeshkar.governance.RuleResult;
import java.util.regex.Pattern;
import tools.jackson.databind.JsonNode;

public final class RegexFieldRule implements ArgumentRule {
  private final String field;
  private final Pattern pattern;

  public RegexFieldRule(String field, Pattern pattern) {
    if (field == null || field.isBlank()) {
      throw new IllegalArgumentException("field must not be blank");
    }
    this.field = field;
    this.pattern = java.util.Objects.requireNonNull(pattern, "pattern");
  }

  @Override
  public RuleResult evaluate(JsonNode arguments) {
    JsonNode value = arguments.get(field);
    if (value == null || !value.isTextual() || !pattern.matcher(value.asText()).matches()) {
      return RuleResult.deny("argument does not match the required pattern: " + field);
    }
    return RuleResult.allow();
  }
}
