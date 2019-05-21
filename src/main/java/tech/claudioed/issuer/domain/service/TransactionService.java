package tech.claudioed.issuer.domain.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import tech.claudioed.issuer.domain.Transaction;
import tech.claudioed.issuer.domain.repository.TransactionRepository;
import tech.claudioed.issuer.domain.service.data.Card;
import tech.claudioed.issuer.domain.service.data.TransactionRequest;

@Service
public class TransactionService {

  private final TransactionRepository transactionRepository;

  private final VaultService vaultService;

  public TransactionService(
      TransactionRepository transactionRepository, VaultService vaultService) {
    this.transactionRepository = transactionRepository;
    this.vaultService = vaultService;
  }

  public Transaction acquire(@NonNull TransactionRequest transactionRequest) {
    final Card card = this.vaultService.token(transactionRequest.getData().getData());

    return null;
  }
}
