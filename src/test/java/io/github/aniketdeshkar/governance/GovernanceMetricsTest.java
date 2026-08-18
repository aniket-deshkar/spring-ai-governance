package io.github.aniketdeshkar.governance;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

class GovernanceMetricsTest {
  @Test
  void recordsDecisionWithBoundedTags() {
    SimpleMeterRegistry registry = new SimpleMeterRegistry();
    GovernanceMetrics metrics = GovernanceMetrics.micrometer(registry);

    metrics.record("transfer", GovernanceDecision.DENY);

    assertThat(
            registry
                .get("spring.ai.governance.decisions")
                .tags("tool", "transfer", "decision", "deny")
                .counter()
                .count())
        .isEqualTo(1);
  }
}
