package tech.claudioed.issuer.domain;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

/** @author claudioed on 2019-05-20. Project issuer */
@Value
@Builder
public class Balance {

  BigDecimal balance;
}
