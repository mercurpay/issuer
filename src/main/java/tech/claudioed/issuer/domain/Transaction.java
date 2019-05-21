package tech.claudioed.issuer.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Transaction {

    @Id
    private String id;

    private BigDecimal value;

    private String card;

    private String customer;

    private String type;

    private LocalDateTime at;

}
