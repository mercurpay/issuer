package tech.claudioed.issuer.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Timer;
import io.nats.client.Connection;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tech.claudioed.issuer.domain.Account;
import tech.claudioed.issuer.domain.Card;
import tech.claudioed.issuer.domain.Transaction;
import tech.claudioed.issuer.domain.exception.AccountNotFound;
import tech.claudioed.issuer.domain.repository.AccountRepository;
import tech.claudioed.issuer.domain.service.data.TransactionRequest;
import tech.claudioed.issuer.domain.service.data.TransactionValue;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static tech.claudioed.issuer.infra.metrics.MetricsConfiguration.REQUEST_PAYMENT;

@Slf4j
@Service
public class PurchaseService {

  private final AccountRepository accountRepository;

  private final VaultService vaultService;

  private final Timer requestPaymentTimer;

  private final Connection connection;

  private final ObjectMapper mapper;

  public PurchaseService(AccountRepository accountRepository,
                         VaultService vaultService,
                         @Qualifier(REQUEST_PAYMENT) Timer requestPaymentTimer,
                         @Qualifier("natsConnection")Connection connection, ObjectMapper mapper) {
    this.accountRepository = accountRepository;
    this.vaultService = vaultService;
    this.requestPaymentTimer = requestPaymentTimer;
    this.connection = connection;
    this.mapper = mapper;
  }

  Transaction acquire(@NonNull TransactionRequest transactionRequest) {
    return this.requestPaymentTimer.record(() ->{
      log.info("Requesting new transaction for token {} ",transactionRequest.getData().getData());
      final Card card = this.vaultService.token(transactionRequest.getData().getData());
      final Optional<Account> accountData = this.accountRepository.findById(card.getCard());
      if(accountData.isPresent()){
        final Account account = accountData.get();
        log.info("Account id {} is ready for acquire",transactionRequest.getData().getData());
        CheckCardBalance.builder()
                .card(account.getCard())
                .transactionValue(TransactionValue.builder().value(transactionRequest.getValue()).build())
                .build()
                .check();
        final Transaction transaction = Transaction.builder().id(UUID.randomUUID().toString()).at(LocalDateTime.now()).card(card.getCard())
                .customer(card.getCustomer()).type(transactionRequest.getType()).status("APPROVED")
                .value(transactionRequest.getValue()).build();
        this.accountRepository.save(account);
        try {
          this.connection.publish("transaction-created",this.mapper.writeValueAsBytes(transaction));
          return transaction;
        } catch (JsonProcessingException e) {
          log.error("Error on json serialize",e);
          throw new RuntimeException("Error on json serialize");
        }
      }else {
        log.error("Account for token {} not found",transactionRequest.getData().getData());
        throw new AccountNotFound();
      }
    });
  }

}
