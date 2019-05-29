package tech.claudioed.issuer.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tech.claudioed.issuer.domain.Account;
import tech.claudioed.issuer.domain.Balance;
import tech.claudioed.issuer.domain.Card;
import tech.claudioed.issuer.domain.OperationType;
import tech.claudioed.issuer.domain.Transaction;
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


  private final Connection connection;

  private final ObjectMapper mapper;

  public ChargeCardService(AccountRepository accountRepository, VaultService vaultService,
                           @Qualifier("natsConnection")Connection connection, ObjectMapper mapper) {
    this.accountRepository = accountRepository;
    this.vaultService = vaultService;
    this.connection = connection;
    this.mapper = mapper;
  }

  Balance charge(@NonNull CardChargeRequest cardChargeRequest){
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
        try {
          this.connection.publish("card-charged",this.mapper.writeValueAsBytes(transaction));
          return account.balance();
        } catch (JsonProcessingException e) {
          log.error("Error on json serialize",e);
          throw new RuntimeException("Error on json serialize");
        }
      }else {
        log.error("Account not found {} ",cardChargeRequest.getData().getData());
        throw new AccountNotFound();
      }
  }
}
