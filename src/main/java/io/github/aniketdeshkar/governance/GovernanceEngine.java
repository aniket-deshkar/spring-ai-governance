package io.github.aniketdeshkar.governance;

import io.github.aniketdeshkar.governance.approval.ApprovalRequest;
import io.github.aniketdeshkar.governance.approval.ApprovalService;
import io.github.aniketdeshkar.governance.approval.ApprovalStatus;
import io.github.aniketdeshkar.governance.audit.GovernanceAuditEvent;
import io.github.aniketdeshkar.governance.audit.GovernanceAuditPublisher;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class GovernanceEngine {
  private final List<ToolPolicy> policies;
  private final ApprovalService approvalService;
  private final GovernanceAuditPublisher auditPublisher;
  private final GovernanceMetrics metrics;

  public GovernanceEngine(
      List<ToolPolicy> policies,
      ApprovalService approvalService,
      GovernanceAuditPublisher auditPublisher,
      GovernanceMetrics metrics) {
    this.policies = List.copyOf(policies);
    this.approvalService = Objects.requireNonNull(approvalService, "approvalService");
    this.auditPublisher = Objects.requireNonNull(auditPublisher, "auditPublisher");
    this.metrics = Objects.requireNonNull(metrics, "metrics");
  }

  public void authorize(ToolInvocation invocation) {
    PolicyDecision approvalDecision = null;
    for (ToolPolicy policy : policies) {
      PolicyDecision decision =
          Objects.requireNonNull(policy.evaluate(invocation), "policy decision");
      if (decision.decision() == GovernanceDecision.DENY) {
        reject(invocation, decision);
      }
      if (decision.decision() == GovernanceDecision.REQUIRE_APPROVAL && approvalDecision == null) {
        approvalDecision = decision;
      }
    }

    if (approvalDecision != null) {
      ApprovalStatus status =
          approvalService.requestApproval(new ApprovalRequest(invocation, approvalDecision));
      if (status != ApprovalStatus.APPROVED) {
        GovernanceDecision finalDecision =
            status == ApprovalStatus.PENDING
                ? GovernanceDecision.REQUIRE_APPROVAL
                : GovernanceDecision.DENY;
        PolicyDecision rejected =
            new PolicyDecision(
                finalDecision,
                "approval-service",
                status == ApprovalStatus.PENDING ? "approval is pending" : "approval was denied");
        reject(invocation, rejected);
      }
      publish(invocation, PolicyDecision.allow("approval-service", "approval was granted"));
      return;
    }

    publish(
        invocation, PolicyDecision.allow("governance-engine", "all policies allowed execution"));
  }

  private void reject(ToolInvocation invocation, PolicyDecision decision) {
    publish(invocation, decision);
    throw new ToolGovernanceException(invocation.id(), decision.decision(), decision.reason());
  }

  private void publish(ToolInvocation invocation, PolicyDecision decision) {
    metrics.record(invocation.tool().name(), decision.decision());
    auditPublisher.publish(
        new GovernanceAuditEvent(
            invocation.id(),
            invocation.tool().name(),
            invocation.principalName(),
            invocation.tool().risk(),
            decision.decision(),
            decision.policy(),
            decision.reason(),
            Instant.now()));
  }
}
