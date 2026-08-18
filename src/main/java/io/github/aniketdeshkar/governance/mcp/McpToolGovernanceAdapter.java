package io.github.aniketdeshkar.governance.mcp;

import io.github.aniketdeshkar.governance.ToolDescriptor;
import io.github.aniketdeshkar.governance.spring.GovernanceToolCallbackFactory;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.ai.tool.ToolCallback;

public final class McpToolGovernanceAdapter {
  private McpToolGovernanceAdapter() {}

  public static List<ToolCallback> wrap(
      List<? extends ToolCallback> callbacks,
      Map<String, ToolDescriptor> descriptors,
      GovernanceToolCallbackFactory factory) {
    Objects.requireNonNull(callbacks, "callbacks");
    return callbacks.stream()
        .map(
            callback -> {
              String name = callback.getToolDefinition().name();
              ToolDescriptor descriptor = descriptors.get(name);
              if (descriptor == null) {
                throw new IllegalArgumentException(
                    "missing governance descriptor for MCP tool: " + name);
              }
              return factory.wrap(callback, descriptor);
            })
        .toList();
  }
}
