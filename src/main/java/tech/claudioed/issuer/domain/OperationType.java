package tech.claudioed.issuer.domain;

import java.math.BigDecimal;
import tech.claudioed.issuer.domain.service.data.TransactionValue;

/** @author claudioed on 2019-05-20. Project issuer */
public enum OperationType {

  PURCHASE {
    @Override
    TransactionValue value(BigDecimal value) {
      return TransactionValue.builder().value(value.negate()).build();
    }
  },

  REVERSAL {
    @Override
    TransactionValue value(BigDecimal value) {
      return TransactionValue.builder().value(value).build();
    }
  },
  CREDIT {
    @Override
    TransactionValue value(BigDecimal value) {
      return TransactionValue.builder().value(value).build();
    }
  };

  abstract TransactionValue value(BigDecimal value);
}
