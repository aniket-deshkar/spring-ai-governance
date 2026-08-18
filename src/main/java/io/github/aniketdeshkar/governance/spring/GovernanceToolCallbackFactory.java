package io.github.aniketdeshkar.governance.spring;

import io.github.aniketdeshkar.governance.GovernanceEngine;
import io.github.aniketdeshkar.governance.ToolDescriptor;
import java.util.Objects;
import org.springframework.ai.tool.ToolCallback;
import tools.jackson.databind.ObjectMapper;

public final class GovernanceToolCallbackFactory {
  private final GovernanceEngine engine;
  private final AuthenticationSupplier authenticationSupplier;
  private final ObjectMapper objectMapper;

  public GovernanceToolCallbackFactory(
      GovernanceEngine engine,
      AuthenticationSupplier authenticationSupplier,
      ObjectMapper objectMapper) {
    this.engine = Objects.requireNonNull(engine, "engine");
    this.authenticationSupplier =
        Objects.requireNonNull(authenticationSupplier, "authenticationSupplier");
    this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
  }

  public ToolCallback wrap(ToolCallback callback, ToolDescriptor descriptor) {
    return new GovernanceToolCallback(
        callback, descriptor, engine, authenticationSupplier, objectMapper);
  }
}
