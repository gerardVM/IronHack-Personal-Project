package bank;

import bank.enums.Roles;
import bank.models.accounts.Checking;
import bank.models.Role;
import bank.models.roles.AccountHolder;
import bank.models.roles.ThirdParty;
// import bank.repositories.AccountHolderRepository;
import bank.repositories.CheckingRepository;
import bank.repositories.RoleRepository;
//import bank.repositories.ThirdPartyRepository;
import bank.repositories.UserRepository;
import bank.services.CheckingService;
import bank.services.RoleService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static bank.enums.Roles.*;
import static bank.enums.Status.ACTIVE;

@Service
public class Testing {
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private CheckingService checkingService;
    @Autowired
    //private AccountHolderRepository accountHolderRepository;
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    //private ThirdPartyRepository thirdPartyRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private Role auxRole;

    public void addExampleData() {
        List<Roles> roles = List.of(ADMIN, ACCOUNT_HOLDER, THIRD_PARTY);
        for (Roles role : roles) {
            if (!roleService.findByRole(role).isPresent()) {
                auxRole = new Role();
                auxRole.setRole(role);
                roleRepository.save(auxRole);
            }
        }

        AccountHolder auxUser = new AccountHolder();
        auxUser.setUsername("AuxUser");
        auxUser.setPassword(passwordEncoder.encode("password"));
        auxUser.setRole(roleService.findByRole(ACCOUNT_HOLDER).get());
        auxUser.setBirthDate(LocalDate.of(1990, 1, 1));
        userRepository.save(auxUser);
        Checking tester = new Checking();
        tester.setBalance(BigDecimal.valueOf(250));
        tester.setPrimaryOwner(auxUser);
        tester.setSecretKey(passwordEncoder.encode("1234"));
        tester.setAccountStatus(ACTIVE);
        checkingRepository.save(tester);


        AccountHolder auxUser2 = new AccountHolder();
        auxUser2.setUsername("AuxAdmin");
        auxUser2.setPassword(passwordEncoder.encode("1234"));
        auxUser2.setRole(roleService.findByRole(ADMIN).get());
        auxUser2.setBirthDate(LocalDate.of(1991, 1, 1));
        userRepository.save(auxUser2);

        AccountHolder auxUser3 = new AccountHolder();
        auxUser3.setUsername("AuxUser3");
        auxUser3.setPassword(passwordEncoder.encode("4321"));
        auxUser3.setRole(roleService.findByRole(ACCOUNT_HOLDER).get());
        auxUser3.setBirthDate(LocalDate.of(1992, 1, 1));
        userRepository.save(auxUser3);
        Checking tester3 = new Checking();
        tester3.setBalance(BigDecimal.valueOf(500));
        tester3.setPrimaryOwner(auxUser3);
        tester3.setSecretKey(passwordEncoder.encode("4321"));
        tester3.setAccountStatus(ACTIVE);
        checkingRepository.save(tester3);

        Checking tester4 = new Checking();
        tester4.setBalance(BigDecimal.valueOf(1000));
        tester4.setSecretKey(passwordEncoder.encode("3232"));
        tester4.setAccountStatus(ACTIVE);
        checkingRepository.save(tester4);
        ThirdParty thirdPartyUser = new ThirdParty();
        thirdPartyUser.setUsername("ThirdPartyUser");
        thirdPartyUser.setPassword(passwordEncoder.encode("3333"));
        thirdPartyUser.setRole(roleService.findByRole(THIRD_PARTY).get());
        thirdPartyUser.setAccount(tester4);
        userRepository.save(thirdPartyUser);


    }
}
