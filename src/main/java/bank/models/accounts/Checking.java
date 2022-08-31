package bank.models.accounts;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
    private final BigDecimal minimumBalance = BigDecimal.valueOf(250);
    @NotNull
    private final BigDecimal monthlyMaintenanceFee = BigDecimal.valueOf(12);
    @NotNull
    private LocalDate creationDate = LocalDate.now();
    @NotNull
    private Status accountStatus;
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
