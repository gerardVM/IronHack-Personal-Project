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
    private BigDecimal minimumBalance;
    @NotNull
    private BigDecimal monthlyMaintenanceFee;
    @NotNull
    @Past
    private LocalDate creationDate;
    @NotNull
    private Status accountStatus;
}
