package io.github.aniketdeshkar.governance;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aniketdeshkar.governance.autoconfigure.GovernanceAutoConfiguration;
import io.github.aniketdeshkar.governance.spring.GovernanceToolCallbackFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class GovernanceAutoConfigurationTest {
  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(GovernanceAutoConfiguration.class));

  @Test
  void providesSecureDefaults() {
    runner.run(
        context -> {
          assertThat(context).hasSingleBean(GovernanceEngine.class);
          assertThat(context).hasSingleBean(GovernanceToolCallbackFactory.class);
        });
  }

  @Test
  void canBeDisabled() {
    runner
        .withPropertyValues("spring.ai.governance.enabled=false")
        .run(context -> assertThat(context).doesNotHaveBean(GovernanceEngine.class));
  }

  @Test
  void rejectsInvalidRiskConfiguration() {
    runner
        .withPropertyValues("spring.ai.governance.approval-threshold=unknown")
        .run(context -> assertThat(context).hasFailed());
  }
}
