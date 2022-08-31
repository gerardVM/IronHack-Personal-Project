package bank.models.accounts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import javax.validation.constraints.*;

/**
 *  This class represents a Credit Card Account.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class CreditCard extends Account {
    @NotNull
    @Max(100000)
    @Min(100)
    private BigDecimal creditLimit = BigDecimal.valueOf(100);
    @NotNull
    @DecimalMax(value = "0.2", message = "The interest rate must be between 0.1 and 0.2")
    @DecimalMin(value = "0.1", message = "The interest rate must be between 0.1 and 0.2")
    private double interestRate = 0.2;
    private LocalDate lastInterestAccrualDate = LocalDate.now();

    @Override
    public BigDecimal getBalance() {
        return claimRewards();
    }

    // Does really a credit card produce interest?
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
