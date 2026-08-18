package io.github.aniketdeshkar.governance;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aniketdeshkar.governance.policy.SpringAuthorizationPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import tools.jackson.databind.ObjectMapper;

class SpringAuthorizationPolicyTest {
  @Test
  void delegatesApplicationAuthorizationToSpringSecurity() {
    SpringAuthorizationPolicy policy =
        new SpringAuthorizationPolicy(
            (authentication, invocation) ->
                new AuthorizationDecision(
                    authentication.get().getName().equals("alice")
                        && invocation.tool().name().equals("transfer")));
    ToolInvocation invocation =
        ToolInvocation.create(
            ToolDescriptor.unrestricted("transfer"),
            new ObjectMapper().readTree("{}"),
            new TestingAuthenticationToken("alice", "n/a"));

    assertThat(policy.evaluate(invocation).decision()).isEqualTo(GovernanceDecision.ALLOW);
  }

  @Test
  void deniedAuthorizationReturnsTypedDecision() {
    SpringAuthorizationPolicy policy =
        new SpringAuthorizationPolicy(
            (authentication, invocation) -> new AuthorizationDecision(false));
    ToolInvocation invocation =
        ToolInvocation.create(
            ToolDescriptor.unrestricted("transfer"), new ObjectMapper().readTree("{}"), null);

    assertThat(policy.evaluate(invocation).decision()).isEqualTo(GovernanceDecision.DENY);
  }
}
