package io.github.aniketdeshkar.governance.approval;

@FunctionalInterface
public interface ApprovalService {
  ApprovalStatus requestApproval(ApprovalRequest request);

  static ApprovalService denyByDefault() {
    return request -> ApprovalStatus.DENIED;
  }
}
