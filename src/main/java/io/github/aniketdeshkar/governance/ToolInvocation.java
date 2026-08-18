package io.github.aniketdeshkar.governance;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import tools.jackson.databind.JsonNode;

public record ToolInvocation(
    String id,
    ToolDescriptor tool,
    JsonNode arguments,
    Authentication authentication,
    Instant occurredAt) {
  public ToolInvocation {
    id = Objects.requireNonNull(id, "id");
    tool = Objects.requireNonNull(tool, "tool");
    arguments = Objects.requireNonNull(arguments, "arguments");
    occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
  }

  public static ToolInvocation create(
      ToolDescriptor tool, JsonNode arguments, Authentication authentication) {
    return new ToolInvocation(
        UUID.randomUUID().toString(), tool, arguments, authentication, Instant.now());
  }

  public String principalName() {
    return authentication == null ? "anonymous" : authentication.getName();
  }
}
