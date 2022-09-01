package bank;

import bank.models.Role;
import bank.models.roles.Admin;
import bank.models.roles.ThirdParty;
//import bank.repositories.AdminRepository;
import bank.repositories.RoleRepository;
//import bank.repositories.ThirdPartyRepository;
//import bank.services.AdminService;
//import bank.services.ThirdPartyService;
import bank.repositories.UserRepository;
import bank.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static bank.enums.Roles.ADMIN;
import static bank.enums.Roles.THIRD_PARTY;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ThirdPartyRepositoryTest {
    @Autowired
    //private ThirdPartyRepository thirdPartyRepository;
    private UserRepository userRepository;

    @Autowired
    // private ThirdPartyService thirdPartyService;
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    ThirdParty tester;
    Role auxRole;

    @BeforeEach
    public void setUp() {
        tester = new ThirdParty();
        tester.setUsername("Tester");
        tester.setPassword(passwordEncoder.encode("$2a$12$p8qaYHmtyYnMgyMja2vsbe8K/vXs9NmjFaqfjBQ6Osro68ygS2ogW"));
        auxRole = new Role();
        auxRole.setRole(THIRD_PARTY);
        tester.setRole(auxRole);
        roleRepository.save(auxRole);
        userRepository.save(tester);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void addNewThirdPartyTest() {
        ThirdParty checker = (ThirdParty) userService.findByUsername(tester.getUsername()).get();
        assertEquals(checker.getUsername(), "Tester");
        assertTrue(passwordEncoder.matches("$2a$12$p8qaYHmtyYnMgyMja2vsbe8K/vXs9NmjFaqfjBQ6Osro68ygS2ogW", checker.getPassword()));
        assertEquals(checker.getRole().getRole(), THIRD_PARTY);
    }

    @Test
    void deleteThirdPartyTest(){
        assertThrows(Exception.class, () -> {
            roleRepository.deleteAll();
        } );
    }
}
