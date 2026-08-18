package io.github.aniketdeshkar.governance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class ToolDescriptorTest {
  @Test
  void readsGovernanceMetadataFromMethod() throws Exception {
    Method method = Tools.class.getDeclaredMethod("deleteAccount", String.class);

    ToolDescriptor descriptor = ToolDescriptor.from(method);

    assertThat(descriptor.name()).isEqualTo("delete-account");
    assertThat(descriptor.risk()).isEqualTo(RiskLevel.CRITICAL);
    assertThat(descriptor.requiredAuthorities()).containsExactly("accounts:delete");
  }

  @Test
  void rejectsMethodsWithoutGovernanceMetadata() throws Exception {
    Method method = Tools.class.getDeclaredMethod("lookup");
    assertThatThrownBy(() -> ToolDescriptor.from(method))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @SuppressWarnings("unused")
  private static final class Tools {
    @GovernedTool(
        value = "delete-account",
        risk = RiskLevel.CRITICAL,
        authorities = "accounts:delete")
    void deleteAccount(String id) {}

    void lookup() {}
  }
}
