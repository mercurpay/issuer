package tech.claudioed.issuer.infra.tracing;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Slf4j
@org.springframework.context.annotation.Configuration
public class TracerProducer {

  @Bean
  public Tracer tracer() {
    log.info("Setting tracer....");
    final JaegerTracer tracer =
        Configuration.fromEnv(System.getenv("JAEGER_SERVICE_NAME")).getTracer();
    log.info("Tracer configured successfully.");
    return tracer;
  }
}
