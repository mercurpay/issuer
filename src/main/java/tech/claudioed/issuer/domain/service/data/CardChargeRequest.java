package tech.claudioed.issuer.domain.service.data;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/** @author claudioed on 2019-05-20. Project issuer */
@Value
@Builder
public class CardChargeRequest {

  TokenData data;

  BigDecimal value;
}
