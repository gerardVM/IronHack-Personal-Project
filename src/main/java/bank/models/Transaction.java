package bank.models;

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
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // TRY FINDING A BETTER WAY TO CREATE IDs
    private Long id;
    @NotNull
    private BigDecimal amount;
    @NotNull
    // @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "fromAccount")
    private Long fromAccountId;
    @NotNull
    // @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "toAccount")
    private Long toAccountId;
    @NotNull
    private String fromUsername;
    @NotNull
    private String toUsername;
    @NotNull
    private String signature;
}