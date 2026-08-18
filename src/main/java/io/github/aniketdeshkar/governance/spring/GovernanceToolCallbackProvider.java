package io.github.aniketdeshkar.governance.spring;

import io.github.aniketdeshkar.governance.ToolDescriptor;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

public final class GovernanceToolCallbackProvider implements ToolCallbackProvider {
  private final ToolCallback[] callbacks;

  public GovernanceToolCallbackProvider(
      ToolCallbackProvider delegate,
      Map<String, ToolDescriptor> descriptors,
      GovernanceToolCallbackFactory factory) {
    Objects.requireNonNull(delegate, "delegate");
    Objects.requireNonNull(descriptors, "descriptors");
    Objects.requireNonNull(factory, "factory");
    this.callbacks =
        Arrays.stream(delegate.getToolCallbacks())
            .map(
                callback -> {
                  String name = callback.getToolDefinition().name();
                  ToolDescriptor descriptor = descriptors.get(name);
                  if (descriptor == null) {
                    throw new IllegalArgumentException(
                        "missing governance descriptor for tool: " + name);
                  }
                  return factory.wrap(callback, descriptor);
                })
            .toArray(ToolCallback[]::new);
  }

  @Override
  public ToolCallback[] getToolCallbacks() {
    return callbacks.clone();
  }
}
