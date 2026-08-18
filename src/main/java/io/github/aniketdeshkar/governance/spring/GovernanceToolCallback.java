package io.github.aniketdeshkar.governance.spring;

import io.github.aniketdeshkar.governance.GovernanceDecision;
import io.github.aniketdeshkar.governance.GovernanceEngine;
import io.github.aniketdeshkar.governance.ToolDescriptor;
import io.github.aniketdeshkar.governance.ToolGovernanceException;
import io.github.aniketdeshkar.governance.ToolInvocation;
import java.util.Objects;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public final class GovernanceToolCallback implements ToolCallback {
  private final ToolCallback delegate;
  private final ToolDescriptor descriptor;
  private final GovernanceEngine engine;
  private final AuthenticationSupplier authenticationSupplier;
  private final ObjectMapper objectMapper;

  public GovernanceToolCallback(
      ToolCallback delegate,
      ToolDescriptor descriptor,
      GovernanceEngine engine,
      AuthenticationSupplier authenticationSupplier,
      ObjectMapper objectMapper) {
    this.delegate = Objects.requireNonNull(delegate, "delegate");
    this.descriptor = Objects.requireNonNull(descriptor, "descriptor");
    this.engine = Objects.requireNonNull(engine, "engine");
    this.authenticationSupplier =
        Objects.requireNonNull(authenticationSupplier, "authenticationSupplier");
    this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
    if (!delegate.getToolDefinition().name().equals(descriptor.name())) {
      throw new IllegalArgumentException("descriptor name must match the tool definition name");
    }
  }

  @Override
  public ToolDefinition getToolDefinition() {
    return delegate.getToolDefinition();
  }

  @Override
  public ToolMetadata getToolMetadata() {
    return delegate.getToolMetadata();
  }

  @Override
  public String call(String toolInput) {
    authorize(toolInput);
    return delegate.call(toolInput);
  }

  @Override
  public String call(String toolInput, ToolContext toolContext) {
    authorize(toolInput);
    return delegate.call(toolInput, toolContext);
  }

  private void authorize(String toolInput) {
    final JsonNode arguments;
    try {
      arguments = objectMapper.readTree(toolInput);
    } catch (JacksonException exception) {
      throw new ToolGovernanceException(
          "unparsed", GovernanceDecision.DENY, "tool arguments are not valid JSON");
    }
    if (!arguments.isObject()) {
      throw new ToolGovernanceException(
          "unparsed", GovernanceDecision.DENY, "tool arguments must be a JSON object");
    }
    engine.authorize(ToolInvocation.create(descriptor, arguments, authenticationSupplier.get()));
  }
}
