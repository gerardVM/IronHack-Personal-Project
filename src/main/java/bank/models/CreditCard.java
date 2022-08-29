package bank.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javamoney.moneta.Money;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 *  This class represents a Credit Card Account.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CreditCard extends Account {
    @NotNull
    private Money creditLimit;
    @NotNull
    private double interestRate;
}
