package bank.models.roles;

import bank.models.Account;
import bank.models.Role;
import bank.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountHolder extends User {
    private LocalDate birthDate;
    private String primaryAddress;
    private String optionalAddress;
    @OneToMany(mappedBy = "primaryOwner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Account> primary = new HashSet<>();
    @OneToMany(mappedBy = "secondaryOwner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Account> secondary = new HashSet<>();
}
