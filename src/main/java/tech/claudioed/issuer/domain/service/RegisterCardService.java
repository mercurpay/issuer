package tech.claudioed.issuer.domain.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.NonNull;
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
@Service
public class RegisterCardService {

  private final AccountRepository accountRepository;

  public RegisterCardService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public Account newCard(@NonNull RequestCardRequest requestCardRequest){
    final Card card = Card.builder().card(requestCardRequest.getCard())
        .customer(requestCardRequest.getCustomer()).issuer(requestCardRequest.getIssuer()).build();
    final Transaction transaction = Transaction.builder().id(UUID.randomUUID().toString())
        .at(LocalDateTime.now()).type(
            OperationType.CREDIT).customer(requestCardRequest.getCustomer())
        .card(requestCardRequest.getCard()).value(requestCardRequest.getBalance()).build();
    card.registerTransaction(transaction);
    final Account newAccount = Account.builder().id(requestCardRequest.getCard()).card(card).build();
    return this.accountRepository.save(newAccount);
  }

}
