package bank.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
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
}
