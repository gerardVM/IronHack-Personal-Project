package bank;

import bank.models.Role;
import bank.models.accounts.StudentChecking;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountRepository;
import bank.repositories.RoleRepository;
import bank.repositories.UserRepository;
import bank.services.AccountService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class StudentCheckingRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    StudentChecking tester;
    AccountHolder auxUser;
    Role auxRole;
    StudentChecking checker;

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
        userRepository.save(auxUser);
        tester = new StudentChecking();
        tester.setBalance(BigDecimal.valueOf(100));
        tester.setPrimaryOwner(auxUser);
        tester.setSecretKey(passwordEncoder.encode("1234"));
        tester.setAccountStatus(ACTIVE);
        accountRepository.save(tester);
        checker = (StudentChecking) accountService.findByPrimaryOwner(tester.getPrimaryOwner()).get();
    }

    @AfterEach
    public void tearDown() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void addNewStudentCheckingAccountTest(){
        assertEquals(100, checker.getBalance().intValue());
        assertEquals(40, checker.getPenaltyFee().intValue());
        assertEquals(checker.getPrimaryOwner().getUsername(), "AuxUser");
        assertTrue(passwordEncoder.matches("password", checker.getPrimaryOwner().getPassword()));
        assertEquals(checker.getPrimaryOwner().getRole().getRole(), ACCOUNT_HOLDER);
        assertTrue(passwordEncoder.matches("1234", checker.getSecretKey()));
        assertTrue(checker.getCreationDate().equals(LocalDate.now()));
        assertEquals(checker.getAccountStatus(),ACTIVE);
    }
    @Test
    void deleteStudentCheckingAccountTest(){
        assertThrows(Exception.class, () -> { userRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { accountRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { userRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { roleRepository.deleteAll(); } );
    }
}
