package tech.claudioed.issuer.domain.service;

import static tech.claudioed.issuer.infra.metrics.MetricsConfiguration.REQUEST_CARD_TIMER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Timer;
import io.nats.client.Connection;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tech.claudioed.issuer.domain.Account;
import tech.claudioed.issuer.domain.Card;
import tech.claudioed.issuer.domain.OperationType;
import tech.claudioed.issuer.domain.Transaction;
import tech.claudioed.issuer.domain.repository.AccountRepository;
import tech.claudioed.issuer.domain.service.data.RequestCardRequest;

/**
 * @author claudioed on 2019-05-20.
 * Project issuer
 */
@Slf4j
@Service
public class RegisterCardService {

  private final AccountRepository accountRepository;

  private final Timer requestCardTimer;

  private final Connection connection;

  private final ObjectMapper mapper;

  public RegisterCardService(AccountRepository accountRepository,
                             @Qualifier(REQUEST_CARD_TIMER) Timer requestCardTimer,
                             @Qualifier("natsConnection") Connection connection, ObjectMapper mapper) {
    this.accountRepository = accountRepository;
    this.requestCardTimer = requestCardTimer;
    this.connection = connection;
    this.mapper = mapper;
  }

  Account newCard(@NonNull RequestCardRequest requestCardRequest){
    return this.requestCardTimer.record(() ->{
      log.info("Requesting new card for customer {} and issuer {} ",requestCardRequest.getCustomer(),requestCardRequest.getIssuer());
      final Card card = Card.builder().card(requestCardRequest.getCard())
              .customer(requestCardRequest.getCustomer()).issuer(requestCardRequest.getIssuer()).build();
      final Transaction transaction = Transaction.builder().id(UUID.randomUUID().toString())
              .at(LocalDateTime.now()).type(
                      OperationType.CREDIT).customer(requestCardRequest.getCustomer())
              .card(requestCardRequest.getCard()).value(requestCardRequest.getBalance()).build();
      card.registerTransaction(transaction);
      final Account newAccount = Account.builder().id(requestCardRequest.getCard()).card(card).build();
      try {
        this.connection.publish("card-created",this.mapper.writeValueAsBytes(newAccount));
        log.info("New card request for customer {}",requestCardRequest.getCustomer());
        return this.accountRepository.save(newAccount);
      } catch (JsonProcessingException e) {
        log.error("Error on json serialize",e);
        throw new RuntimeException("Error on json serialize");
      }
    });
  }

}
