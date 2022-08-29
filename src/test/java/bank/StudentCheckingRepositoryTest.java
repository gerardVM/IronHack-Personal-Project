package bank;

import bank.models.Role;
import bank.models.StudentChecking;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountHolderRepository;
import bank.repositories.RoleRepository;
import bank.repositories.StudentCheckingRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class StudentCheckingRepositoryTest {
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    @Autowired
    private StudentCheckingService studentCheckingService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private RoleRepository roleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    StudentChecking tester;
    AccountHolder auxUser;
    Role auxRole;

    @BeforeEach
    public void setUp() {
        auxRole = new Role();
        auxRole.setRole(ACCOUNT_HOLDER);
        roleRepository.save(auxRole);
        auxUser = new AccountHolder();
        auxUser.setUsername("AuxUser");
        auxUser.setPassword(passwordEncoder.encode("password"));
        auxUser.setRole(auxRole);
        accountHolderRepository.save(auxUser);
        tester = new StudentChecking();
        tester.setBalance(BigDecimal.valueOf(100));
        tester.setPenaltyFee(BigDecimal.valueOf(1000));
        tester.setPrimaryOwner(auxUser);
        tester.setSecretKey(passwordEncoder.encode("1234"));
        tester.setCreationDate(LocalDate.of(2020, 1, 1));
        tester.setAccountStatus(ACTIVE);
        studentCheckingRepository.save(tester);
    }

    @AfterEach
    public void tearDown() {
        studentCheckingRepository.deleteAll();
        accountHolderRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void addNewStudentCheckingAccountTest(){
        StudentChecking checker = studentCheckingService.findByPrimaryOwner(tester.getPrimaryOwner()).get();
        assertEquals(100, checker.getBalance().intValue());
        assertEquals(1000, checker.getPenaltyFee().intValue());
        assertEquals(checker.getPrimaryOwner().getUsername(), "AuxUser");
        assertTrue(passwordEncoder.matches("password", checker.getPrimaryOwner().getPassword()));
        assertEquals(checker.getPrimaryOwner().getRole().getRole(), ACCOUNT_HOLDER);
        assertTrue(passwordEncoder.matches("1234", checker.getSecretKey()));
        assertTrue(checker.getCreationDate().equals(LocalDate.of(2020, 1, 1)));
        assertEquals(checker.getAccountStatus(),ACTIVE);
        tester.setCreationDate(LocalDate.of(2023, 1, 2));
        assertThrows(Exception.class, () -> { studentCheckingRepository.save(tester); });
    }
    @Test
    void deleteStudentCheckingAccountTest(){
        assertThrows(Exception.class, () -> {
            accountHolderRepository.deleteAll();
        } );
    }
}
