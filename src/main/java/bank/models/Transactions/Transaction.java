package bank.models.Transactions;

import bank.models.roles.AccountHolder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance( strategy = InheritanceType.JOINED )
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // TRY FINDING A BETTER WAY TO CREATE IDs
    private Long id;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Long fromAccountId;
    @NotNull
    private Long toAccountId;
    @NotNull
    private String fromUsername;
    @NotNull
    private String toUsername;
}