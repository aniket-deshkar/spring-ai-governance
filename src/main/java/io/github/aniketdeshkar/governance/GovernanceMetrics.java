package io.github.aniketdeshkar.governance;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.Objects;

@FunctionalInterface
public interface GovernanceMetrics {
  void record(String toolName, GovernanceDecision decision);

  static GovernanceMetrics noOp() {
    return (toolName, decision) -> {};
  }

  static GovernanceMetrics micrometer(MeterRegistry registry) {
    Objects.requireNonNull(registry, "registry");
    return (toolName, decision) ->
        registry
            .counter(
                "spring.ai.governance.decisions",
                "tool",
                toolName,
                "decision",
                decision.name().toLowerCase(java.util.Locale.ROOT))
            .increment();
  }
}
