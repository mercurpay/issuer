package tech.claudioed.issuer.domain.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import tech.claudioed.issuer.domain.Account;
import tech.claudioed.issuer.domain.Balance;
import tech.claudioed.issuer.domain.OperationType;
import tech.claudioed.issuer.domain.Transaction;
import tech.claudioed.issuer.domain.exception.AccountNotFound;
import tech.claudioed.issuer.domain.repository.AccountRepository;
import tech.claudioed.issuer.domain.service.data.CardChargeRequest;

/**
 * @author claudioed on 2019-05-20.
 * Project issuer
 */
@Service
public class ChargeCardService {

  private final AccountRepository accountRepository;

  public ChargeCardService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public Balance charge(@NonNull CardChargeRequest cardChargeRequest){
    final Optional<Account> accountData = this.accountRepository.findById(cardChargeRequest.getCard());
    if(accountData.isPresent()){
      final Account account = accountData.get();
      final Transaction transaction = Transaction.builder().id(UUID.randomUUID().toString())
          .at(LocalDateTime.now()).type(
              OperationType.CREDIT).customer(cardChargeRequest.getCustomer()).value(cardChargeRequest.getValue())
          .card(cardChargeRequest.getCard()).build();
      account.registerTransaction(transaction);
      this.accountRepository.save(account);
      return account.balance();
    }else {
      throw new AccountNotFound();
    }
  }
}
