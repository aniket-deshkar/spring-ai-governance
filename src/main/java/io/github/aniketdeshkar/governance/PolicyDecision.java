package io.github.aniketdeshkar.governance;

import java.util.Objects;

public record PolicyDecision(GovernanceDecision decision, String policy, String reason) {
  public PolicyDecision {
    decision = Objects.requireNonNull(decision, "decision");
    policy = Objects.requireNonNull(policy, "policy");
    reason = Objects.requireNonNull(reason, "reason");
  }

  public static PolicyDecision allow(String policy, String reason) {
    return new PolicyDecision(GovernanceDecision.ALLOW, policy, reason);
  }

  public static PolicyDecision deny(String policy, String reason) {
    return new PolicyDecision(GovernanceDecision.DENY, policy, reason);
  }

  public static PolicyDecision requireApproval(String policy, String reason) {
    return new PolicyDecision(GovernanceDecision.REQUIRE_APPROVAL, policy, reason);
  }
}
