package bank;

import bank.enums.Roles;
import bank.models.accounts.CreditCard;
import bank.models.Role;
import bank.models.roles.AccountHolder;
import bank.repositories.AccountRepository;
import bank.repositories.RoleRepository;
import bank.repositories.UserRepository;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CreditCardRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    CreditCard tester;
    AccountHolder auxUser;
    Role auxRole;
    CreditCard checker;

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
        tester = new CreditCard();
        tester.setBalance(BigDecimal.valueOf(100));
        tester.setPrimaryOwner(auxUser);
        tester.setSecretKey(passwordEncoder.encode("1234"));
        // creditLimit has a set value of 100, so it is not necessary to set it here
        // interestRate has a set value of 0.2, so it is not necessary to set it here
        accountRepository.save(tester);
        checker = (CreditCard) accountRepository.findByPrimaryOwner(tester.getPrimaryOwner()).get();
    }

    @AfterEach
    public void tearDown() {
        accountRepository.deleteAll();
        userRepository.deleteAll();
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
        assertThrows(Exception.class, () -> { userRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { accountRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { userRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { roleRepository.deleteAll(); } );
    }

    @Test
    void constraintsOfCreditCardAccount(){
        tester.setCreditLimit(BigDecimal.valueOf(99));
        assertThrows(Exception.class, () -> { accountRepository.save(tester); } );
        tester.setCreditLimit(checker.getCreditLimit());
        assertDoesNotThrow(() -> { accountRepository.save(tester); } );

        tester.setCreditLimit(BigDecimal.valueOf(100001));
        assertThrows(Exception.class, () -> { accountRepository.save(tester); } );
        tester.setCreditLimit(checker.getCreditLimit());
        assertDoesNotThrow(() -> { accountRepository.save(tester); } );

        tester.setInterestRate(0.099);
        assertThrows(Exception.class, () -> { accountRepository.save(tester); } );
        tester.setInterestRate(checker.getInterestRate());
        assertDoesNotThrow(() -> { accountRepository.save(tester); } );

        tester.setInterestRate(0.201);
        assertThrows(Exception.class, () -> { accountRepository.save(tester); } );
        tester.setInterestRate(checker.getInterestRate());
        assertDoesNotThrow(() -> { accountRepository.save(tester); } );
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
