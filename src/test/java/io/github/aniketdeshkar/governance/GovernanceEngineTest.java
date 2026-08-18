package io.github.aniketdeshkar.governance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aniketdeshkar.governance.approval.ApprovalService;
import io.github.aniketdeshkar.governance.audit.GovernanceAuditEvent;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class GovernanceEngineTest {
  @Test
  void denyTakesPrecedenceAndApprovalIsNotRequested() {
    List<GovernanceAuditEvent> events = new ArrayList<>();
    ApprovalService approvals =
        request -> {
          throw new AssertionError("approval must not be requested after a deny");
        };
    GovernanceEngine engine =
        new GovernanceEngine(
            List.of(
                invocation -> PolicyDecision.requireApproval("risk", "high risk"),
                invocation -> PolicyDecision.deny("tenant", "wrong tenant")),
            approvals,
            events::add,
            GovernanceMetrics.noOp());

    assertThatThrownBy(() -> engine.authorize(invocation()))
        .isInstanceOf(ToolGovernanceException.class)
        .hasMessage("wrong tenant");
    assertThat(events).singleElement().extracting(GovernanceAuditEvent::policy).isEqualTo("tenant");
  }

  @Test
  void allAllowPoliciesEmitOneFinalAuditEvent() {
    List<GovernanceAuditEvent> events = new ArrayList<>();
    GovernanceEngine engine =
        new GovernanceEngine(
            List.of(invocation -> PolicyDecision.allow("tenant", "matched")),
            ApprovalService.denyByDefault(),
            events::add,
            GovernanceMetrics.noOp());

    engine.authorize(invocation());

    assertThat(events)
        .singleElement()
        .satisfies(
            event -> {
              assertThat(event.decision()).isEqualTo(GovernanceDecision.ALLOW);
              assertThat(event.toolName()).isEqualTo("read-profile");
            });
  }

  private static ToolInvocation invocation() {
    return ToolInvocation.create(
        ToolDescriptor.unrestricted("read-profile"), new ObjectMapper().readTree("{}"), null);
  }
}
