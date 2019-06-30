package tech.claudioed.issuer.infra.grpc;

import io.grpc.MethodDescriptor;
import io.grpc.ServerBuilder;
import io.opentracing.Tracer;
import io.opentracing.contrib.OperationNameConstructor;
import io.opentracing.contrib.ServerTracingInterceptor;
import org.lognet.springboot.grpc.GRpcServerBuilderConfigurer;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GRpcOpentracingInterceptor extends GRpcServerBuilderConfigurer {

  private final Tracer tracer;

  public GRpcOpentracingInterceptor(Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public void configure(ServerBuilder<?> serverBuilder) {
    final ServerTracingInterceptor tracingInterceptor =
        new ServerTracingInterceptor.Builder(tracer)
            .withVerbosity()
            .withOperationName(
                new OperationNameConstructor() {
                  @Override
                  public <ReqT, RespT> String constructOperationName(
                      MethodDescriptor<ReqT, RespT> method) {
                    return Optional.ofNullable(method.getFullMethodName())
                        .filter(fullName -> fullName.contains("/"))
                        .map(fullName -> fullName.split("/"))
                        .map(strings -> strings[1])
                        .orElse(method.getFullMethodName());
                  }
                })
            .build();
    serverBuilder.intercept(tracingInterceptor);
  }
}
