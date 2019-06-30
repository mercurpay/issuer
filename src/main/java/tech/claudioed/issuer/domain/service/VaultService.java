package tech.claudioed.issuer.domain.service;

import io.opentracing.ActiveSpan;
import io.opentracing.Tracer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import tech.claudioed.issuer.domain.Card;
import vault.DataByToken;
import vault.Token;
import vault.VaultServiceGrpc;

@Slf4j
@Service
public class VaultService {

  private final VaultServiceGrpc.VaultServiceBlockingStub vaultingStub;

  private final Tracer tracer;

  public VaultService(VaultServiceGrpc.VaultServiceBlockingStub vaultingStub, Tracer tracer) {
    this.vaultingStub = vaultingStub;
    this.tracer = tracer;
  }

  public Card token(@NonNull String token) {
    log.info("Opening token {} ", token);
    final Token tokenRequest = Token.newBuilder().setValue(token).build();
    final ActiveSpan activeSpan = tracer.buildSpan("vault")
            .asChildOf(tracer.activeSpan())
            .withTag("token", token)
            .startActive();

    final DataByToken data = this.vaultingStub.fromToken(tokenRequest);
    log.info("Token decrypted successfully {} ", token);
    activeSpan.setTag("customer", data.getCustomerId()).setTag("issuer", data.getIssuer());
    activeSpan.close();
    return Card.builder()
        .card(data.getCard())
        .customer(data.getCustomerId())
        .issuer(data.getIssuer())
        .build();
  }
}
