package bank;

import bank.models.CreditCard;
import bank.models.Role;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountHolderRepository;
import bank.repositories.CreditCardRepository;
import bank.repositories.RoleRepository;
import bank.services.CreditCardService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static bank.enums.Roles.ACCOUNT_HOLDER;
import static bank.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CreditCardRepositoryTest {

    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private CreditCardService creditCardService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private RoleRepository roleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    CreditCard tester;
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
        tester = new CreditCard();
        tester.setBalance(BigDecimal.valueOf(100));
        tester.setPenaltyFee(BigDecimal.valueOf(1000));
        tester.setPrimaryOwner(auxUser);
        tester.setCreditLimit(BigDecimal.valueOf(10000));
        tester.setInterestRate(0.07);
        creditCardRepository.save(tester);
    }

    @AfterEach
    public void tearDown() {
        creditCardRepository.deleteAll();
        accountHolderRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void addNewCreditCardAccountTest(){
        CreditCard checker = creditCardRepository.findByPrimaryOwner(tester.getPrimaryOwner()).get();
        assertEquals(100, checker.getBalance().intValue());
        assertEquals(1000, checker.getPenaltyFee().intValue());
        assertEquals(checker.getPrimaryOwner().getUsername(), "AuxUser");
        assertTrue(passwordEncoder.matches("password", checker.getPrimaryOwner().getPassword()));
        assertEquals(checker.getPrimaryOwner().getRole().getRole(), ACCOUNT_HOLDER);
        assertEquals(checker.getSecondaryOwner(), null);
        assertEquals(10000, checker.getCreditLimit().intValue());
        assertEquals(0.07, checker.getInterestRate());
    }

    @Test
    void deleteCreditCardAccountTest(){
        assertThrows(Exception.class, () -> {
            accountHolderRepository.deleteAll();
        } );
    }
}
