package tech.claudioed.issuer.infra;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vault.VaultServiceGrpc;

@Configuration
public class VaultServiceProducers {

  private final String vaultSvcUrl;

  private final Integer vaultSvcPort;

  public VaultServiceProducers(
      @Value("${vault.service.host}") String vaultSvcUrl, @Value("${vault.service.port}") Integer vaultSvcPort) {
    this.vaultSvcUrl = vaultSvcUrl;
    this.vaultSvcPort = vaultSvcPort;
  }

  @Bean("vaultManagedChannel")
  public ManagedChannel vaultManagedChannel(){
      return ManagedChannelBuilder.forAddress(this.vaultSvcUrl,this.vaultSvcPort).usePlaintext().build();
  }

  @Bean("vaultServiceStub")
  public VaultServiceGrpc.VaultServiceBlockingStub channel(ManagedChannel managedChannel){
      return VaultServiceGrpc.newBlockingStub(managedChannel);
  }

}
