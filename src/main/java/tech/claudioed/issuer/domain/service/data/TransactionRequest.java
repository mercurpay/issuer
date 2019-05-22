package tech.claudioed.issuer.domain.service.data;

import lombok.Builder;
import lombok.Value;
import tech.claudioed.issuer.domain.OperationType;

import java.math.BigDecimal;

@Value
@Builder
public class TransactionRequest {

  TokenData data;

  BigDecimal value;

  OperationType type;

}
