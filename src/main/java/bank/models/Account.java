package bank.models;

import bank.models.roles.AccountHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javamoney.moneta.Money;


import javax.persistence.Embeddable;
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
@AllArgsConstructor
public abstract class Account {
    @NotNull
    private Money balance;
    @NotNull
    private AccountHolder primaryOwner;
    // Optional?
    private AccountHolder secondaryOwner;
    @NotNull
    private Money penaltyFee;
}
