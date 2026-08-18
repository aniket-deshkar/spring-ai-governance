package io.github.aniketdeshkar.governance.spring;

import org.springframework.security.core.Authentication;

@FunctionalInterface
public interface AuthenticationSupplier {
  Authentication get();
}
