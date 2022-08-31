package bank.models.Transactions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class RegularTransaction extends Transaction {
    @NotNull
    private String signature;
}

