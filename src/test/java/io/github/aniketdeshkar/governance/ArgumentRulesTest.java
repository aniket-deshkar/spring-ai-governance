package io.github.aniketdeshkar.governance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aniketdeshkar.governance.rule.NumericRangeRule;
import io.github.aniketdeshkar.governance.rule.RegexFieldRule;
import io.github.aniketdeshkar.governance.rule.RequiredFieldRule;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class ArgumentRulesTest {
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void requiredFieldRejectsBlankValues() {
    assertThat(new RequiredFieldRule("account").evaluate(json("{\"account\":\" \"}")).allowed())
        .isFalse();
  }

  @Test
  void regexRuleAcceptsMatchingText() {
    RuleResult result =
        new RegexFieldRule("region", Pattern.compile("eu|us"))
            .evaluate(json("{\"region\":\"eu\"}"));
    assertThat(result.allowed()).isTrue();
  }

  @Test
  void numericRangeIncludesBoundaries() {
    NumericRangeRule rule = new NumericRangeRule("amount", 1, 10);
    assertThat(rule.evaluate(json("{\"amount\":1}")).allowed()).isTrue();
    assertThat(rule.evaluate(json("{\"amount\":10}")).allowed()).isTrue();
  }

  @Test
  void invalidRangeConfigurationFailsFast() {
    assertThatThrownBy(() -> new NumericRangeRule("amount", 10, 1))
        .isInstanceOf(IllegalArgumentException.class);
  }

  private JsonNode json(String value) {
    return mapper.readTree(value);
  }
}
