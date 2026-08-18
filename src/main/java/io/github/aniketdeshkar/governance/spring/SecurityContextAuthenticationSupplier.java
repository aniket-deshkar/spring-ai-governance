package io.github.aniketdeshkar.governance.spring;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityContextAuthenticationSupplier implements AuthenticationSupplier {
  @Override
  public Authentication get() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}
