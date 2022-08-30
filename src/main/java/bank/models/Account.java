package bank.models;

import bank.models.roles.AccountHolder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 *  This class represents an abstract Account.
 *  It is the core class of the bank system.
 *
 * @author Gerard
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance( strategy = InheritanceType.JOINED )
public abstract class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private BigDecimal balance;
    @NotNull
    private static BigDecimal penaltyFee = BigDecimal.valueOf(40);
    private boolean penaltyApplied = false;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "primaryOwner")
    private AccountHolder primaryOwner;
    @ManyToOne
    @JoinColumn(name = "secondaryOwner")
    private AccountHolder secondaryOwner;

    // CONSULTA: ¿Por qué los getters de parámetros estáticos dejaron de generarse?
    public BigDecimal getPenaltyFee() {
        return this.penaltyFee;
    }
}
