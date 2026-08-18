package io.github.aniketdeshkar.governance.audit;

@FunctionalInterface
public interface GovernanceAuditPublisher {
  void publish(GovernanceAuditEvent event);

  static GovernanceAuditPublisher noOp() {
    return event -> {};
  }
}
