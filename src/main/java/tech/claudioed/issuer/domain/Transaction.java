package tech.claudioed.issuer.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.claudioed.issuer.domain.service.data.TransactionValue;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

  private String id;

  private BigDecimal value;

  private String card;

  private String customer;

  private OperationType type;

  private LocalDateTime at;

  private String status;

  public TransactionValue value(){
    return this.type.value(this.value);
  }

}
