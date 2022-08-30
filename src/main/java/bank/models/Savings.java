package bank.models;

import bank.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.validation.constraints.*;
import java.math.BigDecimal;
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
    @Max(1000)
    @Min(100)
    private BigDecimal minimumBalance = BigDecimal.valueOf(1000);
    @NotNull
    @DecimalMax(value = "0.5", message = "The interest rate must be between 0 and 0.5")
    private double interestRate = 0.0025;
    @NotNull
    @Past
    private LocalDate creationDate;
    @NotNull
    private Status accountStatus;

    // We are overriding the setBalance conditions to apply penalization in case balance < minimum but just once
    @Override
    public void setBalance(BigDecimal balance) {
        if (balance.compareTo(this.minimumBalance) < 0 && !super.isPenaltyApplied()) {
            super.setBalance(balance.subtract(super.getPenaltyFee()));
            super.setPenaltyApplied(true);
        } else if (balance.compareTo(this.minimumBalance) < 0 && super.isPenaltyApplied()) {
            super.setBalance(balance);
        } else {
            super.setBalance(balance);
            super.setPenaltyApplied(false);
        }
    }
}


