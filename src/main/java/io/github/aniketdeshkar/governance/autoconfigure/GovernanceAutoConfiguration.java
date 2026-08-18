package io.github.aniketdeshkar.governance.autoconfigure;

import io.github.aniketdeshkar.governance.GovernanceEngine;
import io.github.aniketdeshkar.governance.GovernanceMetrics;
import io.github.aniketdeshkar.governance.ToolPolicy;
import io.github.aniketdeshkar.governance.approval.ApprovalService;
import io.github.aniketdeshkar.governance.audit.GovernanceAuditPublisher;
import io.github.aniketdeshkar.governance.audit.SpringEventAuditPublisher;
import io.github.aniketdeshkar.governance.policy.ArgumentPolicy;
import io.github.aniketdeshkar.governance.policy.RequiredAuthorityPolicy;
import io.github.aniketdeshkar.governance.policy.RiskApprovalPolicy;
import io.github.aniketdeshkar.governance.spring.AuthenticationSupplier;
import io.github.aniketdeshkar.governance.spring.GovernanceToolCallbackFactory;
import io.github.aniketdeshkar.governance.spring.SecurityContextAuthenticationSupplier;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration
@ConditionalOnClass(ToolCallback.class)
@ConditionalOnProperty(
    prefix = "spring.ai.governance",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@EnableConfigurationProperties(GovernanceProperties.class)
public class GovernanceAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  ApprovalService governanceApprovalService() {
    return ApprovalService.denyByDefault();
  }

  @Bean
  @ConditionalOnMissingBean
  GovernanceAuditPublisher governanceAuditPublisher(ApplicationEventPublisher publisher) {
    return new SpringEventAuditPublisher(publisher);
  }

  @Bean
  @ConditionalOnMissingBean
  GovernanceMetrics governanceMetrics(ObjectProvider<MeterRegistry> meterRegistry) {
    MeterRegistry registry = meterRegistry.getIfAvailable();
    return registry == null ? GovernanceMetrics.noOp() : GovernanceMetrics.micrometer(registry);
  }

  @Bean
  @ConditionalOnMissingBean
  AuthenticationSupplier governanceAuthenticationSupplier() {
    return new SecurityContextAuthenticationSupplier();
  }

  @Bean
  @ConditionalOnMissingBean
  GovernanceEngine governanceEngine(
      GovernanceProperties properties,
      ObjectProvider<ToolPolicy> customPolicies,
      ApprovalService approvalService,
      GovernanceAuditPublisher auditPublisher,
      GovernanceMetrics metrics) {
    List<ToolPolicy> policies = new ArrayList<>();
    policies.add(new RequiredAuthorityPolicy());
    policies.add(new ArgumentPolicy());
    customPolicies.orderedStream().forEach(policies::add);
    policies.add(new RiskApprovalPolicy(properties.getApprovalThreshold()));
    return new GovernanceEngine(policies, approvalService, auditPublisher, metrics);
  }

  @Bean
  @ConditionalOnMissingBean
  GovernanceToolCallbackFactory governanceToolCallbackFactory(
      GovernanceEngine engine,
      AuthenticationSupplier authenticationSupplier,
      ObjectProvider<ObjectMapper> objectMapper) {
    return new GovernanceToolCallbackFactory(
        engine, authenticationSupplier, objectMapper.getIfAvailable(ObjectMapper::new));
  }
}
