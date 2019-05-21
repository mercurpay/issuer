package tech.claudioed.issuer.domain.service.data;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

/** @author claudioed on 2019-05-20. Project issuer */
@Value
@Builder
public class RequestCardRequest {

  String card;

  String customer;

  String issuer;

  BigDecimal balance;
}
