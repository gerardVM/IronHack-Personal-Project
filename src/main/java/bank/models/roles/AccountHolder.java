package bank.models.roles;

import bank.auxiliar.Address;
import bank.models.accounts.Account;
import bank.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Entity
public class AccountHolder extends User {
    @NotNull
    private LocalDate birthDate;
    @Embedded
    private Address primaryAddress;
    private String mailingAddress;
    @OneToMany(mappedBy = "primaryOwner", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Account> primary = new HashSet<>();
    @OneToMany(mappedBy = "secondaryOwner", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Account> secondary = new HashSet<>();
}
