package bank.models;

import bank.models.roles.AccountHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javamoney.moneta.Money;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
    @NotNull
    private Long id;
    @NotNull
    private Money balance;
    @NotNull
    private Money penaltyFee;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "primaryOwner")
    private AccountHolder primaryOwner;
    @ManyToOne
    @JoinColumn(name = "secondaryOwner")
    private AccountHolder secondaryOwner;
}
