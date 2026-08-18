package io.github.aniketdeshkar.governance;

import tools.jackson.databind.JsonNode;

@FunctionalInterface
public interface ArgumentRule {
  RuleResult evaluate(JsonNode arguments);
}
