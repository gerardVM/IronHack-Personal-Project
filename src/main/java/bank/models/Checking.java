package bank.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import bank.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *  This class represents a Checking Account.
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Checking extends Account {
    @NotNull
    private String secretKey;
    @NotNull
    private final BigDecimal minimumBalance = BigDecimal.valueOf(250);
    @NotNull
    private final BigDecimal monthlyMaintenanceFee = BigDecimal.valueOf(12);
    @NotNull
    private LocalDate creationDate = LocalDate.now();
    @NotNull
    private Status accountStatus;

    // public BigDecimal getMinimumBalance() {
    //    return this.minimumBalance;
    // }

    //public BigDecimal getMonthlyMaintenanceFee() {
    //    return this.monthlyMaintenanceFee;
    //}

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
