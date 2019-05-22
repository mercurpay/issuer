package tech.claudioed.issuer.domain.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import tech.claudioed.issuer.domain.Account;
import tech.claudioed.issuer.domain.Card;
import tech.claudioed.issuer.domain.Transaction;
import tech.claudioed.issuer.domain.exception.AccountNotFound;
import tech.claudioed.issuer.domain.repository.AccountRepository;
import tech.claudioed.issuer.domain.service.data.TransactionRequest;
import tech.claudioed.issuer.domain.service.data.TransactionValue;

@Service
public class PurchaseService {

  private final AccountRepository accountRepository;

  private final VaultService vaultService;

  public PurchaseService(AccountRepository accountRepository,
      VaultService vaultService) {
    this.accountRepository = accountRepository;
    this.vaultService = vaultService;
  }

  public Transaction acquire(@NonNull TransactionRequest transactionRequest) {
    final Card card = this.vaultService.token(transactionRequest.getData().getData());
    final Optional<Account> accountData = this.accountRepository.findById(card.getCard());
    if(accountData.isPresent()){
      final Account account = accountData.get();
      CheckCardBalance.builder()
          .card(account.getCard())
          .transactionValue(TransactionValue.builder().value(transactionRequest.getValue()).build())
          .build()
          .check();
      final Transaction transaction = Transaction.builder().id(UUID.randomUUID().toString()).at(LocalDateTime.now()).card(card.getCard())
          .customer(card.getCustomer()).type(transactionRequest.getType()).status("APPROVED")
          .value(transactionRequest.getValue()).build();
      this.accountRepository.save(account);
      return transaction;
    }else {
      throw new AccountNotFound();
    }
  }

}
