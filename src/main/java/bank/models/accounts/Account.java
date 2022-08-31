package bank.models.accounts;

import bank.models.User;
import bank.models.roles.AccountHolder;

import bank.models.roles.ThirdParty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 *  This class represents an abstract Account.
 *  It is the core class of the bank system.
 *
 * @author Gerard
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private BigDecimal balance;
    @NotNull
    private static BigDecimal penaltyFee = BigDecimal.valueOf(40);
    private boolean penaltyApplied = false;
    @NotNull
    private String secretKey;
    @ManyToOne
    @JoinColumn(name = "primaryOwner")
    private AccountHolder primaryOwner;
    @ManyToOne
    @JoinColumn(name = "secondaryOwner")
    private AccountHolder secondaryOwner;
    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    private ThirdParty thirdParty;
    public BigDecimal getPenaltyFee() {
        return this.penaltyFee;
    }
}
