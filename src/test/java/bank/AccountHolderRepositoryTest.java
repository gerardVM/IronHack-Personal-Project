package bank;

import bank.auxiliar.Address;
import bank.models.Role;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountHolderRepository;
import bank.repositories.RoleRepository;
import bank.services.AccountHolderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static bank.enums.Roles.ACCOUNT_HOLDER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountHolderRepositoryTest {

    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private AccountHolderService accountHolderService;

    @Autowired
    private RoleRepository roleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    AccountHolder tester;
    Role auxRole;
    AccountHolder checker;
    Address auxAddress;

    @BeforeEach
    void setUp() {
        auxRole = new Role();
        auxRole.setRole(ACCOUNT_HOLDER);
        roleRepository.save(auxRole);
        auxAddress = new Address();
        auxAddress.setStreet("street");
        tester = new AccountHolder();
        tester.setUsername("Tester");
        tester.setPassword(passwordEncoder.encode("password"));
        tester.setBirthDate(LocalDate.of(1990, 1, 1));
        tester.setPrimaryAddress(auxAddress);
        tester.setMailingAddress("Test Address 2");
        tester.setRole(auxRole);
        accountHolderRepository.save(tester);
        checker = accountHolderService.findByUsername(tester.getUsername()).get();
    }

    @AfterEach
    void tearDown() {
        accountHolderRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void addNewAccountHolderTest() {
        assertEquals(checker.getUsername(), "Tester");
        assertTrue(passwordEncoder.matches("password", checker.getPassword()));
        assertNotEquals(checker.getPassword(), passwordEncoder.encode("password"));
        assertEquals(checker.getRole().getRole(), ACCOUNT_HOLDER);
        assertEquals(checker.getBirthDate(), LocalDate.of(1990, 1, 1));
        assertEquals(checker.getPrimaryAddress().getStreet(), "street");
        assertEquals(checker.getMailingAddress(), "Test Address 2");
    }

    @Test
    void deleteAccountHolderTest(){
        assertThrows(Exception.class, () -> { roleRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { accountHolderRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { roleRepository.deleteAll(); } );
    }
}
