package tech.claudioed.issuer.infra.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.NonNull;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MetricsConfiguration {

  private static final String ROOT_TAG = "operation";
  private static final String INFRA_LABEL = "infra";

  public static final String REQUEST_CARD_TIMER = "requestCardTimer";
  public static final String REQUEST_CHARGE = "requestChargeTimer";
  public static final String REQUEST_PAYMENT = "requestPaymentTimer";

  private final MeterRegistry meterRegistry;

  public MetricsConfiguration(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Bean
  MeterRegistryCustomizer<MeterRegistry> registerCommonTags(Environment environment) {
    final String applicationName = environment.getProperty("spring.application.name");
    return registry -> registry.config().commonTags(
        "app_name", applicationName,
        "env", environment.getActiveProfiles()[0])
        .namingConvention(new MetricsNamingConvention(applicationName));
  }

  @Bean(name = REQUEST_CARD_TIMER)
  public Timer requestCardTimer() {
    return createTimer(ROOT_TAG, ROOT_TAG, "request_card", "type", INFRA_LABEL);
  }

  @Bean(name = REQUEST_CHARGE)
  public Timer requestChargeTimer() {
    return createTimer(ROOT_TAG, ROOT_TAG, "request_charge", "type", INFRA_LABEL);
  }


  @Bean(name = REQUEST_PAYMENT)
  public Timer requestPaymentTimer() {
    return createTimer(ROOT_TAG, ROOT_TAG, "request_payment", "type", INFRA_LABEL);
  }

  private Timer createTimer(@NonNull String tagName, String... labels) {
    return meterRegistry.timer(tagName, labels);
  }

}