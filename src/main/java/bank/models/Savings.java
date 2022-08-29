package bank.models;

import bank.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *  This class represents a Savings Account.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Savings extends Account{
    @NotNull
    private String secretKey;
    @NotNull
    private BigDecimal minimumBalance;
    @NotNull
    private double interestRate;
    @NotNull
    @Past
    private LocalDate creationDate;
    @NotNull
    private Status accountStatus;
}
