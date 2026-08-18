package io.github.aniketdeshkar.governance.rule;

import io.github.aniketdeshkar.governance.ArgumentRule;
import io.github.aniketdeshkar.governance.RuleResult;
import tools.jackson.databind.JsonNode;

public final class NumericRangeRule implements ArgumentRule {
  private final String field;
  private final double minimum;
  private final double maximum;

  public NumericRangeRule(String field, double minimum, double maximum) {
    if (field == null || field.isBlank()) {
      throw new IllegalArgumentException("field must not be blank");
    }
    if (minimum > maximum) {
      throw new IllegalArgumentException("minimum must not exceed maximum");
    }
    this.field = field;
    this.minimum = minimum;
    this.maximum = maximum;
  }

  @Override
  public RuleResult evaluate(JsonNode arguments) {
    JsonNode value = arguments.get(field);
    if (value == null || !value.isNumber()) {
      return RuleResult.deny("argument must be numeric: " + field);
    }
    double number = value.asDouble();
    if (number < minimum || number > maximum) {
      return RuleResult.deny("argument is outside the allowed range: " + field);
    }
    return RuleResult.allow();
  }
}
