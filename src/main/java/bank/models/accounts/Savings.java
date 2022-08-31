package bank.models.accounts;

import bank.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Max(1000)
    @Min(100)
    private BigDecimal minimumBalance = BigDecimal.valueOf(1000);
    @NotNull
    @DecimalMax(value = "0.5", message = "The interest rate must be between 0 and 0.5")
    private double interestRate = 0.0025;
    private LocalDate lastInterestAccrualDate = LocalDate.now();
    @NotNull
    private LocalDate creationDate = LocalDate.now();
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

    @Override
    public BigDecimal getBalance() {
        return claimRewards();
    }

    public BigDecimal claimRewards() {
        BigDecimal balance = super.getBalance();
        super.setBalance(balance.multiply (BigDecimal.valueOf
                                (Math.pow(1 + (this.interestRate/12), getEpochs())) )
                .setScale(10, RoundingMode.HALF_UP));
        return super.getBalance();
    }

    private int getEpochs() {
        // This method returns the number of epochs (Months) that have passed since last accrual date
        LocalDate today = LocalDate.now();
        int epochs = 0;
        while (lastInterestAccrualDate.isBefore(today.minusMonths(1))) {
            epochs++;
            setLastInterestAccrualDate(lastInterestAccrualDate.plusMonths(1));
            today = LocalDate.now();
        } return epochs;
    }
}


