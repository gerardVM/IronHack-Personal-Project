package bank;

import bank.enums.Roles;
import bank.services.AccountService;
import bank.services.NewAccounts;
import bank.models.accounts.Checking;
import bank.models.Role;
import bank.models.roles.AccountHolder;
import bank.repositories.*;
import bank.services.RoleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static bank.enums.Roles.*;
import static bank.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NewAccountsTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private NewAccounts newAccounts;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    Checking tester;
    AccountHolder auxUser;
    Role auxRole;
    Checking checker;

    @BeforeEach
    void setUp() {
        List<Roles> roles = List.of(ADMIN, ACCOUNT_HOLDER, THIRD_PARTY);
        for (Roles role : roles) {
            if (!roleService.findByRole(role).isPresent()) {
                auxRole = new Role();
                auxRole.setRole(role);
                roleRepository.save(auxRole);
            }
        }
        auxUser = new AccountHolder();
        auxUser.setUsername("AuxUser");
        auxUser.setPassword(passwordEncoder.encode("password"));
        auxUser.setRole(roleService.findByRole(ACCOUNT_HOLDER).get());
        auxUser.setBirthDate(LocalDate.of(1990, 1, 1));
        userRepository.save(auxUser);
        tester = new Checking();
        tester.setBalance(BigDecimal.valueOf(250));
        tester.setPrimaryOwner(auxUser);
        tester.setSecretKey(passwordEncoder.encode("1234"));
        tester.setCreationDate(LocalDate.now());
        tester.setAccountStatus(ACTIVE);
        accountRepository.save(tester);
        checker = (Checking) accountRepository.findByPrimaryOwner(tester.getPrimaryOwner()).get();
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void ageTest() {
        newAccounts = new NewAccounts();
        assertEquals(24, newAccounts.getAge(LocalDate.of(1998, 8, 29)));
        assertEquals(23, newAccounts.getAge(LocalDate.of(1998, 12, 31)));
    }

    @Test
    void newCheckingAccountTest(){
        accountRepository.deleteAll();
        auxUser.setBirthDate(LocalDate.of(1998, 8, 29));
        tester.setPrimaryOwner(auxUser);

        accountRepository.deleteAll();
        auxUser.setBirthDate(LocalDate.of(1998, 12, 29));
        tester.setPrimaryOwner(auxUser);
    }
}
