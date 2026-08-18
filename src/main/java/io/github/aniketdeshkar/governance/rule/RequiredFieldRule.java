package io.github.aniketdeshkar.governance.rule;

import io.github.aniketdeshkar.governance.ArgumentRule;
import io.github.aniketdeshkar.governance.RuleResult;
import tools.jackson.databind.JsonNode;

public final class RequiredFieldRule implements ArgumentRule {
  private final String field;

  public RequiredFieldRule(String field) {
    if (field == null || field.isBlank()) {
      throw new IllegalArgumentException("field must not be blank");
    }
    this.field = field;
  }

  @Override
  public RuleResult evaluate(JsonNode arguments) {
    JsonNode value = arguments.get(field);
    if (value == null || value.isNull() || (value.isString() && value.asText().isBlank())) {
      return RuleResult.deny("required argument is missing: " + field);
    }
    return RuleResult.allow();
  }
}
