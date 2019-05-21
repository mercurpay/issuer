package tech.claudioed.issuer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author claudioed on 2019-05-20.
 * Project issuer
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class Account {

  @Id
  private String id;

  private Card card;

  public Account registerTransaction(@NonNull Transaction transaction){
    this.card.registerTransaction(transaction);
    return this;
  }

  public Balance balance(){
    return this.card.balance();
  }

}
