package tech.claudioed.issuer.domain;

import lombok.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

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
