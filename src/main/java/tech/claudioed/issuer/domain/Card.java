package tech.claudioed.issuer.domain;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tech.claudioed.issuer.domain.Transaction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

  private String card;

  private String customer;

  private String issuer;

  private Set<Transaction> transactions;

  public Balance balance() {
    final Optional<BigDecimal> result = this.transactions.stream().map((x) -> x.value().getValue()).reduce(BigDecimal::add);
    return Balance.builder().balance(result.orElse(BigDecimal.ZERO)).build();
  }

  public Card registerTransaction(@NonNull Transaction transaction){
    this.transactions.add(transaction);
    return this;
  }

}
