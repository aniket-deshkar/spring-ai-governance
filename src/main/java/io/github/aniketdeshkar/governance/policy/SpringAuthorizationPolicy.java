package io.github.aniketdeshkar.governance.policy;

import io.github.aniketdeshkar.governance.PolicyDecision;
import io.github.aniketdeshkar.governance.ToolInvocation;
import io.github.aniketdeshkar.governance.ToolPolicy;
import java.util.Objects;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;

public final class SpringAuthorizationPolicy implements ToolPolicy {
  private final AuthorizationManager<ToolInvocation> authorizationManager;

  public SpringAuthorizationPolicy(AuthorizationManager<ToolInvocation> authorizationManager) {
    this.authorizationManager =
        Objects.requireNonNull(authorizationManager, "authorizationManager");
  }

  @Override
  public PolicyDecision evaluate(ToolInvocation invocation) {
    AuthorizationResult result =
        authorizationManager.authorize(() -> invocation.authentication(), invocation);
    return result != null && result.isGranted()
        ? PolicyDecision.allow("spring-security", "authorization manager granted access")
        : PolicyDecision.deny("spring-security", "authorization manager denied access");
  }
}
