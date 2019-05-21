package tech.claudioed.issuer.domain.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import tech.claudioed.issuer.domain.service.data.Card;
import vault.DataByToken;
import vault.Token;
import vault.VaultServiceGrpc;

@Service
public class VaultService {

  private final VaultServiceGrpc.VaultServiceBlockingStub vaultService;

  public VaultService(VaultServiceGrpc.VaultServiceBlockingStub vaultService) {
    this.vaultService = vaultService;
  }

  public Card token(@NonNull String token) {
    final Token tokenRequest = Token.newBuilder().setValue(token).build();
    final DataByToken data = this.vaultService.fromToken(tokenRequest);
    return Card.builder()
            .card(data.getCard())
            .customer(data.getCustomerId())
            .issuer(data.getIssuer())
            .build();
  }
}
