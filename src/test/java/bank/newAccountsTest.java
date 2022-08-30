package bank;

import bank.controllers.NewAccounts;
import bank.models.Checking;
import bank.models.Role;
import bank.models.roles.AccountHolder;
import bank.repositories.*;
import bank.services.CheckingService;
import bank.services.StudentCheckingService;
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

@SpringBootTest
public class newAccountsTest {

    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private CheckingService checkingService;
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    @Autowired
    private StudentCheckingService studentCheckingService;

    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private RoleRepository roleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    Checking tester;
    AccountHolder auxUser;
    Role auxRole;
    Checking checker;
    NewAccounts newAccounts;

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
        tester.setCreationDate(LocalDate.now());
        tester.setAccountStatus(ACTIVE);
        checkingRepository.save(tester);
        checker = checkingRepository.findByPrimaryOwner(tester.getPrimaryOwner()).get();
    }

    @AfterEach
    void tearDown() {
        checkingRepository.deleteAll();
        studentCheckingRepository.deleteAll();
        accountHolderRepository.deleteAll();
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
        newAccounts = new NewAccounts();

        checkingRepository.deleteAll();
        auxUser.setBirthDate(LocalDate.of(1998, 8, 29));
        tester.setPrimaryOwner(auxUser);

        if (newAccounts.getAge(tester.getPrimaryOwner().getBirthDate()) < 24) {
            studentCheckingRepository.save(newAccounts.newStudentChecking(tester));
        } else {
            checkingRepository.save(tester);
        }

        assertDoesNotThrow(() -> { checkingService.findByPrimaryOwner(checker.getPrimaryOwner()).orElseThrow(); } );
        assertThrows(Exception.class, () -> { studentCheckingService.findByPrimaryOwner(checker.getPrimaryOwner()).orElseThrow(); } );

        checkingRepository.deleteAll();
        auxUser.setBirthDate(LocalDate.of(1998, 12, 29));
        tester.setPrimaryOwner(auxUser);

        if (newAccounts.getAge(tester.getPrimaryOwner().getBirthDate()) < 24) {
            studentCheckingRepository.save(newAccounts.newStudentChecking(tester));
        } else {
            checkingRepository.save(tester);
        }

        assertDoesNotThrow(() -> { studentCheckingService.findByPrimaryOwner(checker.getPrimaryOwner()).orElseThrow(); } );
        assertThrows(Exception.class, () -> { checkingService.findByPrimaryOwner(checker.getPrimaryOwner()).orElseThrow(); } );
    }
}
