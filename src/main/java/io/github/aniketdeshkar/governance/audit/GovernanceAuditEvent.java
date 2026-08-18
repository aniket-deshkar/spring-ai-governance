package io.github.aniketdeshkar.governance.audit;

import io.github.aniketdeshkar.governance.GovernanceDecision;
import io.github.aniketdeshkar.governance.RiskLevel;
import java.time.Instant;

public record GovernanceAuditEvent(
    String invocationId,
    String toolName,
    String principal,
    RiskLevel risk,
    GovernanceDecision decision,
    String policy,
    String reason,
    Instant occurredAt) {}
