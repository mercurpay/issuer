package tech.claudioed.issuer.domain.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.claudioed.issuer.domain.*;
import tech.claudioed.issuer.domain.exception.AccountNotFound;
import tech.claudioed.issuer.domain.repository.AccountRepository;
import tech.claudioed.issuer.domain.service.data.CardChargeRequest;

/**
 * @author claudioed on 2019-05-20.
 * Project issuer
 */
@Slf4j
@Service
public class ChargeCardService {

  private final AccountRepository accountRepository;

  private final VaultService vaultService;

  public ChargeCardService(AccountRepository accountRepository, VaultService vaultService) {
    this.accountRepository = accountRepository;
    this.vaultService = vaultService;
  }

  public Balance charge(@NonNull CardChargeRequest cardChargeRequest){
    log.info("Requesting charge for token {} ",cardChargeRequest.getData().getData());
    final Card card = this.vaultService.token(cardChargeRequest.getData().getData());
    final Optional<Account> accountData = this.accountRepository.findById(card.getCard());
    if(accountData.isPresent()){
      final Account account = accountData.get();
      log.info("Charging account id {} ", account.getId());
      final Transaction transaction = Transaction.builder().id(UUID.randomUUID().toString())
          .at(LocalDateTime.now()).type(
              OperationType.CREDIT).customer(card.getCustomer()).value(cardChargeRequest.getValue())
          .card(card.getCard()).build();
      account.registerTransaction(transaction);
      this.accountRepository.save(account);
      return account.balance();
    }else {
      log.error("Account not found {} ",cardChargeRequest.getData().getData());
      throw new AccountNotFound();
    }
  }
}
