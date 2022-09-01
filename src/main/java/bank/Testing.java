package bank;

import bank.enums.Roles;
import bank.models.accounts.Checking;
import bank.models.Role;
import bank.models.roles.AccountHolder;
import bank.models.roles.ThirdParty;
import bank.repositories.AccountRepository;
import bank.repositories.RoleRepository;
import bank.repositories.UserRepository;
import bank.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static bank.enums.Roles.*;
import static bank.enums.Status.ACTIVE;
import static bank.enums.Status.FROZEN;

@Service
public class Testing {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private Role auxdemoRole;

    public void addExampleData() {
        List<Roles> roles = List.of(ADMIN, ACCOUNT_HOLDER, THIRD_PARTY);
        for (Roles role : roles) {
            if (!roleService.findByRole(role).isPresent()) {
                auxdemoRole = new Role();
                auxdemoRole.setRole(role);
                roleRepository.save(auxdemoRole);
            }
        }

        AccountHolder auxdemoUser = new AccountHolder();
        auxdemoUser.setUsername("auxdemoUser");
        auxdemoUser.setPassword(passwordEncoder.encode("password"));
        auxdemoUser.setRole(roleService.findByRole(ACCOUNT_HOLDER).get());
        auxdemoUser.setBirthDate(LocalDate.of(1990, 1, 1));
        userRepository.save(auxdemoUser);
        Checking demo = new Checking();
        demo.setBalance(BigDecimal.valueOf(250));
        demo.setPrimaryOwner(auxdemoUser);
        demo.setSecretKey(passwordEncoder.encode("1234"));
        demo.setAccountStatus(ACTIVE);
        accountRepository.save(demo);


        AccountHolder auxdemoUser2 = new AccountHolder();
        auxdemoUser2.setUsername("auxdemoAdmin");
        auxdemoUser2.setPassword(passwordEncoder.encode("1234"));
        auxdemoUser2.setRole(roleService.findByRole(ADMIN).get());
        auxdemoUser2.setBirthDate(LocalDate.of(1991, 1, 1));
        userRepository.save(auxdemoUser2);

        AccountHolder auxdemoUser3 = new AccountHolder();
        auxdemoUser3.setUsername("auxdemoUser3");
        auxdemoUser3.setPassword(passwordEncoder.encode("4321"));
        auxdemoUser3.setRole(roleService.findByRole(ACCOUNT_HOLDER).get());
        auxdemoUser3.setBirthDate(LocalDate.of(1992, 1, 1));
        userRepository.save(auxdemoUser3);
        Checking demo3 = new Checking();
        demo3.setBalance(BigDecimal.valueOf(500));
        demo3.setPrimaryOwner(auxdemoUser3);
        demo3.setSecretKey(passwordEncoder.encode("4321"));
        demo3.setAccountStatus(FROZEN);
        accountRepository.save(demo3);

        Checking demo4 = new Checking();
        demo4.setBalance(BigDecimal.valueOf(1000));
        demo4.setSecretKey(passwordEncoder.encode("3232"));
        demo4.setAccountStatus(ACTIVE);
        accountRepository.save(demo4);
        ThirdParty thirdPartyUser = new ThirdParty();
        thirdPartyUser.setUsername("ThirdPartyUser");
        thirdPartyUser.setPassword(passwordEncoder.encode("3333"));
        thirdPartyUser.setRole(roleService.findByRole(THIRD_PARTY).get());
        thirdPartyUser.setAccount(demo4);
        userRepository.save(thirdPartyUser);


    }
}
