package bank.models;

import bank.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javamoney.moneta.Money;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

/**
 *  This class represents a Savings Account.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Savings extends Account{
    @NotNull
    private String secretKey;
    @NotNull
    private Money minimumBalance;
    @NotNull
    private double interestRate;
    @NotNull
    private LocalDate creationDate;
    @NotNull
    private Status accountStatus;
}
