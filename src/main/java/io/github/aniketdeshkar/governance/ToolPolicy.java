package io.github.aniketdeshkar.governance;

@FunctionalInterface
public interface ToolPolicy {
  PolicyDecision evaluate(ToolInvocation invocation);
}
