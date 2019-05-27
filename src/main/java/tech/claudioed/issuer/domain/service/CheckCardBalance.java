package tech.claudioed.issuer.domain.service;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import tech.claudioed.issuer.domain.Card;
import tech.claudioed.issuer.domain.exception.InsufficientFunds;
import tech.claudioed.issuer.domain.service.data.TransactionValue;

/** @author claudioed on 2019-05-20. Project issuer */
@Slf4j
@Value
@Builder
class CheckCardBalance {

  Card card;

  TransactionValue transactionValue;

  Boolean check() {
    log.info("Checking funds for customer {}",this.card.getCustomer());
    if (this.card.balance().getBalance().compareTo(transactionValue.getValue()) <= 0) {
      log.error("Customer {} has not available funds to pay {}",this.card.getCustomer(),this.transactionValue.getValue());
      throw new InsufficientFunds();
    }
    log.info("Customer is able to pay {}",this.transactionValue.getValue());
    return true;
  }
}
