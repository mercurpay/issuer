package tech.claudioed.issuer.domain.service.data;

import java.math.BigDecimal;
import lombok.Data;
import tech.claudioed.issuer.domain.OperationType;

@Data
public class TransactionRequest {

  private TokenData data;

  private BigDecimal value;

  private OperationType type;

}
