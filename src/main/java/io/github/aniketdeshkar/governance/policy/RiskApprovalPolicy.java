package io.github.aniketdeshkar.governance.policy;

import io.github.aniketdeshkar.governance.PolicyDecision;
import io.github.aniketdeshkar.governance.RiskLevel;
import io.github.aniketdeshkar.governance.ToolInvocation;
import io.github.aniketdeshkar.governance.ToolPolicy;
import java.util.Objects;

public final class RiskApprovalPolicy implements ToolPolicy {
  private final RiskLevel threshold;

  public RiskApprovalPolicy(RiskLevel threshold) {
    this.threshold = Objects.requireNonNull(threshold, "threshold");
  }

  @Override
  public PolicyDecision evaluate(ToolInvocation invocation) {
    if (invocation.tool().risk().ordinal() >= threshold.ordinal()) {
      return PolicyDecision.requireApproval(
          "risk-approval", "tool risk meets the approval threshold");
    }
    return PolicyDecision.allow("risk-approval", "tool risk is below the approval threshold");
  }
}
