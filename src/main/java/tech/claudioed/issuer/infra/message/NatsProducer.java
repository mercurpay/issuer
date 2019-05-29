package tech.claudioed.issuer.infra.message;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class NatsProducer {

  private final String natsUser;

  private final String natsPass;

  private final String natsHost;

  public NatsProducer(
      @Value("${nats.user}") String natsUser,
      @Value("${nats.pass}") String natsPass,
      @Value("${nats.host}") String natsHost) {
    this.natsUser = natsUser;
    this.natsPass = natsPass;
    this.natsHost = natsHost;
  }

  @SneakyThrows
  @Bean("natsConnection")
  public Connection natsConnection() {
    return Nats.connect(new Options.Builder()
        .connectionTimeout(Duration.ofSeconds(2))
        .pingInterval(Duration.ofSeconds(10))
        .reconnectWait(Duration.ofSeconds(1))
        .userInfo(natsUser,natsPass)
        .maxReconnects(-1)
        .reconnectBufferSize(-1)
        .connectionName(System.getenv("HOSTNAME"))
        .server(natsHost)
        .build());
  }

}