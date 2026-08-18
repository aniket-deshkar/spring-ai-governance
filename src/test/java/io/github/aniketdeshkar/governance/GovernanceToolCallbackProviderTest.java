package io.github.aniketdeshkar.governance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aniketdeshkar.governance.approval.ApprovalService;
import io.github.aniketdeshkar.governance.spring.GovernanceToolCallbackFactory;
import io.github.aniketdeshkar.governance.spring.GovernanceToolCallbackProvider;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import tools.jackson.databind.ObjectMapper;

class GovernanceToolCallbackProviderTest {
  @Test
  void wrapsEveryProviderCallback() {
    AtomicInteger calls = new AtomicInteger();
    GovernanceToolCallbackProvider provider =
        new GovernanceToolCallbackProvider(
            ToolCallbackProvider.from(callback("lookup", calls)),
            Map.of("lookup", ToolDescriptor.unrestricted("lookup")),
            factory());

    assertThat(provider.getToolCallbacks()).hasSize(1);
    assertThat(provider.getToolCallbacks()[0].call("{}")).isEqualTo("ok");
    assertThat(calls).hasValue(1);
  }

  @Test
  void missingDescriptorFailsClosedAtRegistration() {
    assertThatThrownBy(
            () ->
                new GovernanceToolCallbackProvider(
                    ToolCallbackProvider.from(callback("lookup", new AtomicInteger())),
                    Map.of(),
                    factory()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("missing governance descriptor");
  }

  private static GovernanceToolCallbackFactory factory() {
    GovernanceEngine engine =
        new GovernanceEngine(
            List.of(), ApprovalService.denyByDefault(), event -> {}, GovernanceMetrics.noOp());
    return new GovernanceToolCallbackFactory(engine, () -> null, new ObjectMapper());
  }

  private static ToolCallback callback(String name, AtomicInteger calls) {
    ToolDefinition definition =
        ToolDefinition.builder()
            .name(name)
            .description("Lookup")
            .inputSchema("{\"type\":\"object\"}")
            .build();
    return new ToolCallback() {
      @Override
      public ToolDefinition getToolDefinition() {
        return definition;
      }

      @Override
      public String call(String input) {
        calls.incrementAndGet();
        return "ok";
      }
    };
  }
}
