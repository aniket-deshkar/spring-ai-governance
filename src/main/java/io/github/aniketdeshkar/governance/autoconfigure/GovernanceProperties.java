package io.github.aniketdeshkar.governance.autoconfigure;

import io.github.aniketdeshkar.governance.RiskLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.ai.governance")
public class GovernanceProperties {
  private boolean enabled = true;
  private RiskLevel approvalThreshold = RiskLevel.HIGH;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public RiskLevel getApprovalThreshold() {
    return approvalThreshold;
  }

  public void setApprovalThreshold(RiskLevel approvalThreshold) {
    if (approvalThreshold == null) {
      throw new IllegalArgumentException("approvalThreshold must not be null");
    }
    this.approvalThreshold = approvalThreshold;
  }
}
