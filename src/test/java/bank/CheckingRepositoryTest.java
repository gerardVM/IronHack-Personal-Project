package bank;

import bank.models.Checking;
import bank.models.Role;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountHolderRepository;
import bank.repositories.CheckingRepository;
import bank.repositories.RoleRepository;
import bank.services.CheckingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

import static bank.enums.Roles.ACCOUNT_HOLDER;
import static bank.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CheckingRepositoryTest {
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private CheckingService checkingService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private RoleRepository roleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    Checking tester;
    AccountHolder auxUser;
    Role auxRole;
    Checking checker;

    @BeforeEach
    void setUp() {
        auxRole = new Role();
        auxRole.setRole(ACCOUNT_HOLDER);
        roleRepository.save(auxRole);
        auxUser = new AccountHolder();
        auxUser.setUsername("AuxUser");
        auxUser.setPassword(passwordEncoder.encode("password"));
        auxUser.setRole(auxRole);
        auxUser.setBirthDate(LocalDate.of(1990, 1, 1));
        accountHolderRepository.save(auxUser);
        tester = new Checking();
        tester.setBalance(BigDecimal.valueOf(250));
        tester.setPrimaryOwner(auxUser);
        tester.setSecretKey(passwordEncoder.encode("1234"));
        tester.setAccountStatus(ACTIVE);
        checkingRepository.save(tester);
        checker = checkingRepository.findByPrimaryOwner(tester.getPrimaryOwner()).get();
    }

    @AfterEach
    void tearDown() {
        checkingRepository.deleteAll();
        accountHolderRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void addNewCheckingAccountTest(){
        assertEquals(250, checker.getBalance().intValue());
        assertEquals(40, checker.getPenaltyFee().intValue());
        assertEquals("AuxUser", checker.getPrimaryOwner().getUsername());
        assertTrue(passwordEncoder.matches("password", checker.getPrimaryOwner().getPassword()));
        assertEquals(ACCOUNT_HOLDER, checker.getPrimaryOwner().getRole().getRole());
        assertTrue(passwordEncoder.matches("1234", checker.getSecretKey()));
        assertEquals(250, checker.getMinimumBalance().intValue());
        assertEquals(12, checker.getMonthlyMaintenanceFee().intValue());
        assertTrue(checker.getCreationDate().equals(LocalDate.now()));
        assertEquals(checker.getAccountStatus(),ACTIVE);
    }
    @Test
    void deleteCreditCardAccountTest(){
        assertThrows(Exception.class, () -> { accountHolderRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { checkingRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { accountHolderRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { roleRepository.deleteAll(); } );
    }


    @Test
    void logicsForCheckingAccount() {
        tester.setBalance(BigDecimal.valueOf(249));
        assertEquals(209, tester.getBalance().intValue());
        tester.setBalance(BigDecimal.valueOf(249));
        assertEquals(249, tester.getBalance().intValue());
        tester.setBalance(BigDecimal.valueOf(250));
        assertEquals(250, tester.getBalance().intValue());
        tester.setBalance(BigDecimal.valueOf(249));
        assertEquals(209, tester.getBalance().intValue());
    }
}
