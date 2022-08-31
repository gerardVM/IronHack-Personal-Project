package bank;

import bank.models.accounts.CreditCard;
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
import java.math.RoundingMode;
import java.time.LocalDate;

import static bank.enums.Roles.ACCOUNT_HOLDER;
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
    CreditCard checker;

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
        tester = new CreditCard();
        tester.setBalance(BigDecimal.valueOf(100));
        tester.setPrimaryOwner(auxUser);
        // creditLimit has a set value of 100, so it is not necessary to set it here
        // interestRate has a set value of 0.2, so it is not necessary to set it here
        creditCardRepository.save(tester);
        checker = creditCardRepository.findByPrimaryOwner(tester.getPrimaryOwner()).get();
    }

    @AfterEach
    public void tearDown() {
        creditCardRepository.deleteAll();
        accountHolderRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void addNewCreditCardAccountTest(){
        assertEquals(100, checker.getBalance().intValue());
        assertEquals(40, checker.getPenaltyFee().intValue());
        assertEquals(checker.getPrimaryOwner().getUsername(), "AuxUser");
        assertTrue(passwordEncoder.matches("password", checker.getPrimaryOwner().getPassword()));
        assertEquals(checker.getPrimaryOwner().getRole().getRole(), ACCOUNT_HOLDER);
        assertEquals(checker.getSecondaryOwner(), null);
        assertEquals(100, checker.getCreditLimit().intValue());
        assertEquals(0.2, checker.getInterestRate());
    }

    @Test
    void deleteSavingsAccountTest(){
        assertThrows(Exception.class, () -> { accountHolderRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { creditCardRepository.deleteAll();       } );
        assertDoesNotThrow(() -> { accountHolderRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { roleRepository.deleteAll();          } );
    }

    @Test
    void constraintsOfCreditCardAccount(){
        tester.setCreditLimit(BigDecimal.valueOf(99));
        assertThrows(Exception.class, () -> { creditCardRepository.save(tester); } );
        tester.setCreditLimit(checker.getCreditLimit());
        assertDoesNotThrow(() -> { creditCardRepository.save(tester); } );

        tester.setCreditLimit(BigDecimal.valueOf(100001));
        assertThrows(Exception.class, () -> { creditCardRepository.save(tester); } );
        tester.setCreditLimit(checker.getCreditLimit());
        assertDoesNotThrow(() -> { creditCardRepository.save(tester); } );

        tester.setInterestRate(0.099);
        assertThrows(Exception.class, () -> { creditCardRepository.save(tester); } );
        tester.setInterestRate(checker.getInterestRate());
        assertDoesNotThrow(() -> { creditCardRepository.save(tester); } );

        tester.setInterestRate(0.201);
        assertThrows(Exception.class, () -> { creditCardRepository.save(tester); } );
        tester.setInterestRate(checker.getInterestRate());
        assertDoesNotThrow(() -> { creditCardRepository.save(tester); } );
    }

    @Test
    void InterestRateTest(){
        tester.setBalance(BigDecimal.valueOf(1000000));
        tester.setLastInterestAccrualDate(LocalDate.of(2022, 9, 20));
        assertEquals( (BigDecimal.valueOf(1000000).multiply(
                        BigDecimal.valueOf(Math.pow(0.2/12+1,0))))
                        .setScale(10, RoundingMode.HALF_UP),
                tester.getBalance());
        tester.setLastInterestAccrualDate(LocalDate.of(2022, 4, 20));
        assertEquals( BigDecimal.valueOf(1000000).multiply(
                                BigDecimal.valueOf(Math.pow(0.2/12+1,4)))
                        .setScale(10, RoundingMode.HALF_UP),
                tester.getBalance());
    }
}
