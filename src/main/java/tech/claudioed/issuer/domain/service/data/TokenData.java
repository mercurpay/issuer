package tech.claudioed.issuer.domain.service.data;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TokenData {

  String data;
}
