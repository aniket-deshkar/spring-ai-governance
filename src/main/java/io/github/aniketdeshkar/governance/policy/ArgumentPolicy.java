package io.github.aniketdeshkar.governance.policy;

import io.github.aniketdeshkar.governance.ArgumentRule;
import io.github.aniketdeshkar.governance.PolicyDecision;
import io.github.aniketdeshkar.governance.RuleResult;
import io.github.aniketdeshkar.governance.ToolInvocation;
import io.github.aniketdeshkar.governance.ToolPolicy;

public final class ArgumentPolicy implements ToolPolicy {
  @Override
  public PolicyDecision evaluate(ToolInvocation invocation) {
    for (ArgumentRule rule : invocation.tool().argumentRules()) {
      RuleResult result = rule.evaluate(invocation.arguments());
      if (!result.allowed()) {
        return PolicyDecision.deny("argument-rule", result.reason());
      }
    }
    return PolicyDecision.allow("argument-rule", "all argument rules passed");
  }
}
