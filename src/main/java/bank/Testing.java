package bank;

import bank.models.Checking;
import bank.models.Role;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountHolderRepository;
import bank.repositories.CheckingRepository;
import bank.repositories.RoleRepository;
import bank.services.CheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static bank.enums.Roles.ACCOUNT_HOLDER;
import static bank.enums.Roles.ADMIN;
import static bank.enums.Status.ACTIVE;

@Service
public class Testing {
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private CheckingService checkingService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private RoleRepository roleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void addExampleData() {
        Role auxRole = new Role();
        auxRole.setRole(ACCOUNT_HOLDER);
        roleRepository.save(auxRole);
        AccountHolder auxUser = new AccountHolder();
        auxUser.setUsername("AuxUser");
        auxUser.setPassword(passwordEncoder.encode("password"));
        auxUser.setRole(auxRole);
        auxUser.setBirthDate(LocalDate.of(1990, 1, 1));
        accountHolderRepository.save(auxUser);
        Checking tester = new Checking();
        tester.setBalance(BigDecimal.valueOf(250));
        tester.setPrimaryOwner(auxUser);
        tester.setSecretKey(passwordEncoder.encode("1234"));
        tester.setAccountStatus(ACTIVE);
        checkingRepository.save(tester);

        Role auxRole2 = new Role();
        auxRole2.setRole(ADMIN);
        roleRepository.save(auxRole2);
        AccountHolder auxUser2 = new AccountHolder();
        auxUser2.setUsername("AuxAdmin");
        auxUser2.setPassword(passwordEncoder.encode("1234"));
        auxUser2.setRole(auxRole2);
        auxUser2.setBirthDate(LocalDate.of(1991, 1, 1));
        accountHolderRepository.save(auxUser2);
    }
}
