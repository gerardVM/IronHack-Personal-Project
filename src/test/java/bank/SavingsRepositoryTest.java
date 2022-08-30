package bank;

import bank.models.Checking;
import bank.models.Role;
import bank.models.Savings;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountHolderRepository;
import bank.repositories.RoleRepository;
import bank.repositories.SavingsRepository;
import bank.services.SavingsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;

import static bank.enums.Roles.ACCOUNT_HOLDER;
import static bank.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class SavingsRepositoryTest {
    @Autowired
    private SavingsRepository savingsRepository;
    @Autowired
    private SavingsService savingsService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private RoleRepository roleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    Savings tester;
    AccountHolder auxUser;
    Role auxRole;
    Savings checker;

    @BeforeEach
    public void setUp() {
        auxRole = new Role();
        auxRole.setRole(ACCOUNT_HOLDER);
        roleRepository.save(auxRole);
        auxUser = new AccountHolder();
        auxUser.setUsername("AuxUser");
        auxUser.setPassword(passwordEncoder.encode("password"));
        auxUser.setRole(auxRole);
        auxUser.setBirthDate(LocalDate.of(1990, 1, 1));
        accountHolderRepository.save(auxUser);
        tester = new Savings();
        tester.setBalance(BigDecimal.valueOf(1000));
        tester.setPrimaryOwner(auxUser);
        tester.setSecretKey(passwordEncoder.encode("1234"));
        // minimumBalance has a default value. So, we don't need to set it.
        // interestRate has a default value. So, we don't need to set it.
        // creationDate has a default value. So, we don't need to set it.
        tester.setAccountStatus(ACTIVE);
        savingsRepository.save(tester);
        checker = savingsRepository.findByPrimaryOwner(tester.getPrimaryOwner()).get();
    }

    @AfterEach
    public void tearDown() {
        savingsRepository.deleteAll();
        accountHolderRepository.deleteAll();
        roleRepository.deleteAll();
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
    void deleteSavingsAccountTest(){
        assertThrows(Exception.class, () -> { accountHolderRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { savingsRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { accountHolderRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { roleRepository.deleteAll(); } );
    }

    @Test
    void constraintsOfSavingsAccount(){
        tester.setMinimumBalance(BigDecimal.valueOf(99));
        assertThrows(Exception.class, () -> { savingsRepository.save(tester); });
        tester.setMinimumBalance(checker.getMinimumBalance());
        assertDoesNotThrow(() -> { savingsRepository.save(tester); });

        tester.setMinimumBalance(BigDecimal.valueOf(1001));
        assertThrows(Exception.class, () -> { savingsRepository.save(tester); });
        tester.setMinimumBalance(checker.getMinimumBalance());
        assertDoesNotThrow(() -> { savingsRepository.save(tester); });

        tester.setInterestRate(0.51);
        assertThrows(Exception.class, () -> { savingsRepository.save(tester); });
        tester.setInterestRate(checker.getInterestRate());
        assertDoesNotThrow(() -> { savingsRepository.save(tester); });
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
