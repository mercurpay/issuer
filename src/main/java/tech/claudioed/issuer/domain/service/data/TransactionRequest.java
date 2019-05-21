package tech.claudioed.issuer.domain.service.data;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {

  private TokenData data;

  private BigDecimal value;

  private String type;

}
