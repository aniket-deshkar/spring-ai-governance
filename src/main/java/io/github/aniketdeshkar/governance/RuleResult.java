package io.github.aniketdeshkar.governance;

import java.util.Objects;

public record RuleResult(boolean allowed, String reason) {
  public RuleResult {
    reason = Objects.requireNonNull(reason, "reason");
  }

  public static RuleResult allow() {
    return new RuleResult(true, "argument rule passed");
  }

  public static RuleResult deny(String reason) {
    return new RuleResult(false, reason);
  }
}
