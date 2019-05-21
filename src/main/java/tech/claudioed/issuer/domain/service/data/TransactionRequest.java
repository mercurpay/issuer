package tech.claudioed.issuer.domain.service.data;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import tech.claudioed.issuer.domain.OperationType;

@Value
@Builder
public class TransactionRequest {

  private TokenData data;

  private BigDecimal value;

  private OperationType type;

}
