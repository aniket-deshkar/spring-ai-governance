package io.github.aniketdeshkar.governance.policy;

import io.github.aniketdeshkar.governance.PolicyDecision;
import io.github.aniketdeshkar.governance.ToolInvocation;
import io.github.aniketdeshkar.governance.ToolPolicy;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;

public final class RequiredAuthorityPolicy implements ToolPolicy {
  @Override
  public PolicyDecision evaluate(ToolInvocation invocation) {
    if (invocation.tool().requiredAuthorities().isEmpty()) {
      return PolicyDecision.allow("required-authority", "tool has no required authorities");
    }
    Authentication authentication = invocation.authentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return PolicyDecision.deny("required-authority", "an authenticated principal is required");
    }
    Set<String> granted =
        authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .collect(Collectors.toUnmodifiableSet());
    if (!granted.containsAll(invocation.tool().requiredAuthorities())) {
      return PolicyDecision.deny("required-authority", "principal lacks a required authority");
    }
    return PolicyDecision.allow("required-authority", "required authorities are present");
  }
}
