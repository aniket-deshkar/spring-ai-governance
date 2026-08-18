package io.github.aniketdeshkar.governance.audit;

import java.util.Objects;
import org.springframework.context.ApplicationEventPublisher;

public final class SpringEventAuditPublisher implements GovernanceAuditPublisher {
  private final ApplicationEventPublisher publisher;

  public SpringEventAuditPublisher(ApplicationEventPublisher publisher) {
    this.publisher = Objects.requireNonNull(publisher, "publisher");
  }

  @Override
  public void publish(GovernanceAuditEvent event) {
    publisher.publishEvent(event);
  }
}
