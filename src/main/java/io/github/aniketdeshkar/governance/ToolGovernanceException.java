package io.github.aniketdeshkar.governance;

public final class ToolGovernanceException extends RuntimeException {
  private final String invocationId;
  private final GovernanceDecision decision;

  public ToolGovernanceException(String invocationId, GovernanceDecision decision, String message) {
    super(message);
    this.invocationId = invocationId;
    this.decision = decision;
  }

  public String invocationId() {
    return invocationId;
  }

  public GovernanceDecision decision() {
    return decision;
  }
}
