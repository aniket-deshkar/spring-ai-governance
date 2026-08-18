package io.github.aniketdeshkar.governance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.aniketdeshkar.governance.approval.ApprovalService;
import io.github.aniketdeshkar.governance.approval.ApprovalStatus;
import io.github.aniketdeshkar.governance.audit.GovernanceAuditEvent;
import io.github.aniketdeshkar.governance.policy.ArgumentPolicy;
import io.github.aniketdeshkar.governance.policy.RequiredAuthorityPolicy;
import io.github.aniketdeshkar.governance.policy.RiskApprovalPolicy;
import io.github.aniketdeshkar.governance.rule.NumericRangeRule;
import io.github.aniketdeshkar.governance.spring.GovernanceToolCallback;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.security.authentication.TestingAuthenticationToken;
import tools.jackson.databind.ObjectMapper;

class GovernanceToolCallbackTest {
  @Test
  void authorizedToolExecutesExactlyOnce() {
    AtomicInteger calls = new AtomicInteger();
    ToolCallback callback =
        governed(
            calls,
            new ToolDescriptor("transfer", RiskLevel.LOW, List.of("payments:write"), List.of()),
            request -> ApprovalStatus.DENIED,
            new TestingAuthenticationToken("alice", "n/a", "payments:write"),
            new ArrayList<>());

    assertThat(callback.call("{\"amount\":25}")).isEqualTo("completed");
    assertThat(calls).hasValue(1);
  }

  @Test
  void missingAuthorityPreventsSideEffect() {
    AtomicInteger calls = new AtomicInteger();
    List<GovernanceAuditEvent> events = new ArrayList<>();
    ToolCallback callback =
        governed(
            calls,
            new ToolDescriptor("transfer", RiskLevel.LOW, List.of("payments:write"), List.of()),
            request -> ApprovalStatus.APPROVED,
            new TestingAuthenticationToken("mallory", "n/a", "payments:read"),
            events);

    assertThatThrownBy(() -> callback.call("{\"amount\":25}"))
        .isInstanceOf(ToolGovernanceException.class)
        .extracting("decision")
        .isEqualTo(GovernanceDecision.DENY);
    assertThat(calls).hasValue(0);
    assertThat(events)
        .singleElement()
        .extracting(GovernanceAuditEvent::principal)
        .isEqualTo("mallory");
  }

  @Test
  void pendingApprovalPreventsSideEffect() {
    AtomicInteger calls = new AtomicInteger();
    ToolCallback callback =
        governed(
            calls,
            new ToolDescriptor("transfer", RiskLevel.HIGH, List.of(), List.of()),
            request -> ApprovalStatus.PENDING,
            new TestingAuthenticationToken("alice", "n/a"),
            new ArrayList<>());

    assertThatThrownBy(() -> callback.call("{\"amount\":25}"))
        .isInstanceOf(ToolGovernanceException.class)
        .extracting("decision")
        .isEqualTo(GovernanceDecision.REQUIRE_APPROVAL);
    assertThat(calls).hasValue(0);
  }

  @Test
  void approvedHighRiskToolExecutes() {
    AtomicInteger calls = new AtomicInteger();
    List<GovernanceAuditEvent> events = new ArrayList<>();
    ToolCallback callback =
        governed(
            calls,
            new ToolDescriptor("transfer", RiskLevel.HIGH, List.of(), List.of()),
            request -> ApprovalStatus.APPROVED,
            new TestingAuthenticationToken("alice", "n/a"),
            events);

    assertThat(callback.call("{\"amount\":25}")).isEqualTo("completed");
    assertThat(calls).hasValue(1);
    assertThat(events)
        .singleElement()
        .extracting(GovernanceAuditEvent::decision)
        .isEqualTo(GovernanceDecision.ALLOW);
  }

  @Test
  void invalidArgumentPreventsSideEffect() {
    AtomicInteger calls = new AtomicInteger();
    ToolDescriptor descriptor =
        new ToolDescriptor(
            "transfer", RiskLevel.LOW, List.of(), List.of(new NumericRangeRule("amount", 1, 100)));
    ToolCallback callback =
        governed(
            calls,
            descriptor,
            request -> ApprovalStatus.APPROVED,
            new TestingAuthenticationToken("alice", "n/a"),
            new ArrayList<>());

    assertThatThrownBy(() -> callback.call("{\"amount\":101}"))
        .isInstanceOf(ToolGovernanceException.class)
        .hasMessageContaining("outside the allowed range");
    assertThat(calls).hasValue(0);
  }

  @Test
  void malformedJsonNeverReachesPolicyOrTool() {
    AtomicInteger calls = new AtomicInteger();
    ToolCallback callback =
        governed(
            calls,
            ToolDescriptor.unrestricted("transfer"),
            request -> ApprovalStatus.APPROVED,
            null,
            new ArrayList<>());

    assertThatThrownBy(() -> callback.call("not-json"))
        .isInstanceOf(ToolGovernanceException.class)
        .hasMessageContaining("valid JSON");
    assertThat(calls).hasValue(0);
  }

  private static ToolCallback governed(
      AtomicInteger calls,
      ToolDescriptor descriptor,
      ApprovalService approvalService,
      TestingAuthenticationToken authentication,
      List<GovernanceAuditEvent> events) {
    GovernanceEngine engine =
        new GovernanceEngine(
            List.of(
                new RequiredAuthorityPolicy(),
                new ArgumentPolicy(),
                new RiskApprovalPolicy(RiskLevel.HIGH)),
            approvalService,
            events::add,
            GovernanceMetrics.noOp());
    return new GovernanceToolCallback(
        fakeTool(calls), descriptor, engine, () -> authentication, new ObjectMapper());
  }

  private static ToolCallback fakeTool(AtomicInteger calls) {
    ToolDefinition definition =
        ToolDefinition.builder()
            .name("transfer")
            .description("Transfer funds")
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
        return "completed";
      }
    };
  }
}
