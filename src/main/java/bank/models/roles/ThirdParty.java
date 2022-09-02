package bank.models.roles;

import bank.models.Role;
import bank.models.User;
import bank.models.accounts.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ThirdParty extends User {
    @OneToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;
}

