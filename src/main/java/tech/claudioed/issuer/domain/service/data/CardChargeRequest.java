package tech.claudioed.issuer.domain.service.data;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

/**
 * @author claudioed on 2019-05-20.
 * Project issuer
 */
@Value
@Builder
public class CardChargeRequest {

  String card;

  String customer;

  String issuer;

  BigDecimal value;

}
