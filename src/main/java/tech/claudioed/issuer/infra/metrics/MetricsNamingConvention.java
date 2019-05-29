package tech.claudioed.issuer.infra.metrics;

import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.prometheus.PrometheusNamingConvention;

public class MetricsNamingConvention extends PrometheusNamingConvention {

  private final String applicationName;

  public MetricsNamingConvention(String applicationName) {
    this.applicationName = applicationName;
  }

  @Override
  public String name(String name, Type type, String baseUnit) {
    return super.name(applicationName + "_" + name, type, baseUnit);
  }

}