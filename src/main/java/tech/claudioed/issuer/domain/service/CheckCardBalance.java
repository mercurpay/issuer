package tech.claudioed.issuer.domain.service;

import lombok.Builder;
import lombok.Value;
import tech.claudioed.issuer.domain.Card;
import tech.claudioed.issuer.domain.exception.InsufficientFunds;
import tech.claudioed.issuer.domain.service.data.TransactionValue;

/** @author claudioed on 2019-05-20. Project issuer */
@Value
@Builder
class CheckCardBalance {

  Card card;

  TransactionValue transactionValue;

  Boolean check() {
    if (this.card.balance().getBalance().compareTo(transactionValue.getValue()) <= 0) {
      throw new InsufficientFunds();
    }
    return true;
  }
}
