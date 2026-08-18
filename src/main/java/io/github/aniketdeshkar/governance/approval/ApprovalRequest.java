package io.github.aniketdeshkar.governance.approval;

import io.github.aniketdeshkar.governance.PolicyDecision;
import io.github.aniketdeshkar.governance.ToolInvocation;
import java.util.Objects;

public record ApprovalRequest(ToolInvocation invocation, PolicyDecision policyDecision) {
  public ApprovalRequest {
    invocation = Objects.requireNonNull(invocation, "invocation");
    policyDecision = Objects.requireNonNull(policyDecision, "policyDecision");
  }
}
