package bank.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import bank.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javamoney.moneta.Money;
import java.time.LocalDate;

/**
 *  This class represents a Checking Account.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Checking extends Account {
    @NotNull
    private String secretKey;
    @NotNull
    private Money minimumBalance;
    @NotNull
    private Money monthlyMaintenanceFee;
    @NotNull
    private LocalDate creationDate;
    @NotNull
    private Status accountStatus;
}
