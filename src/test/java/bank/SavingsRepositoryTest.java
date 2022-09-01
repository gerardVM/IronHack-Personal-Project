package bank;

import bank.enums.Roles;
import bank.models.Role;
import bank.models.accounts.Savings;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountRepository;
import bank.repositories.RoleRepository;
import bank.repositories.UserRepository;
import bank.services.AccountService;
import bank.services.RoleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static bank.enums.Roles.*;
import static bank.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class SavingsRepositoryTest {
    @Autowired
    // private AccountHolderRepository accountHolderRepository;
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    Savings tester;
    AccountHolder auxUser;
    Role auxRole;
    Savings checker;

    @BeforeEach
    public void setUp() {
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
        tester = new Savings();
        tester.setBalance(BigDecimal.valueOf(1000));
        tester.setPrimaryOwner(auxUser);
        tester.setSecretKey(passwordEncoder.encode("1234"));
        // minimumBalance has a default value. So, we don't need to set it.
        // interestRate has a default value. So, we don't need to set it.
        // creationDate has a default value. So, we don't need to set it.
        tester.setAccountStatus(ACTIVE);
        accountRepository.save(tester);
        checker = (Savings) accountService.findByPrimaryOwner(tester.getPrimaryOwner()).get();
    }

    @AfterEach
    public void tearDown() {
        accountRepository.deleteById(checker.getId());
        userRepository.deleteById(checker.getPrimaryOwner().getId());
        roleRepository.deleteById(checker.getPrimaryOwner().getRole().getId());
    }

    @Test
    void addNewSavingsAccountTest(){
        assertEquals(1000, checker.getBalance().intValue());
        assertEquals(40, checker.getPenaltyFee().intValue());
        assertEquals("AuxUser", checker.getPrimaryOwner().getUsername() );
        assertTrue  (passwordEncoder.matches("password", checker.getPrimaryOwner().getPassword()));
        assertEquals(ACCOUNT_HOLDER, checker.getPrimaryOwner().getRole().getRole());
        assertTrue  (passwordEncoder.matches("1234", checker.getSecretKey()));
        assertEquals(1000, checker.getMinimumBalance().intValue());
        assertEquals(0.0025, checker.getInterestRate());
        assertTrue  (checker.getCreationDate().equals(LocalDate.now()));
        assertEquals(ACTIVE, checker.getAccountStatus());
    }

    @Test
    void constraintsOfSavingsAccount(){
        tester.setMinimumBalance(BigDecimal.valueOf(99));
        assertThrows(Exception.class, () -> { accountRepository.save(tester); });
        tester.setMinimumBalance(checker.getMinimumBalance());
        assertDoesNotThrow(() -> { accountRepository.save(tester); });

        tester.setMinimumBalance(BigDecimal.valueOf(1001));
        assertThrows(Exception.class, () -> { accountRepository.save(tester); });
        tester.setMinimumBalance(checker.getMinimumBalance());
        assertDoesNotThrow(() -> { accountRepository.save(tester); });

        tester.setInterestRate(0.51);
        assertThrows(Exception.class, () -> { accountRepository.save(tester); });
        tester.setInterestRate(checker.getInterestRate());
        assertDoesNotThrow(() -> { accountRepository.save(tester); });
    }

    @Test
    void logicsForSavingsAccount() {
        tester.setBalance(BigDecimal.valueOf(999));
        assertEquals(959, tester.getBalance().intValue());
        tester.setBalance(BigDecimal.valueOf(999));
        assertEquals(999, tester.getBalance().intValue());
        tester.setBalance(BigDecimal.valueOf(1000));
        assertEquals(1000, tester.getBalance().intValue());
        tester.setBalance(BigDecimal.valueOf(999));
        assertEquals(959, tester.getBalance().intValue());
    }

    @Test
    void InterestRateTest(){
        tester.setBalance(BigDecimal.valueOf(1000000));
        tester.setLastInterestAccrualDate(LocalDate.of(2022, 9, 20));
        assertEquals( (BigDecimal.valueOf(1000000).multiply(
                        BigDecimal.valueOf(Math.pow(0.0025/12+1,0))))
                        .setScale(10, RoundingMode.HALF_UP),
                    tester.getBalance());
        tester.setLastInterestAccrualDate(LocalDate.of(2022, 4, 20));
        assertEquals( BigDecimal.valueOf(1000000).multiply(
                        BigDecimal.valueOf(Math.pow(0.0025/12+1,4)))
                        .setScale(10, RoundingMode.HALF_UP),
                tester.getBalance());
    }
}
