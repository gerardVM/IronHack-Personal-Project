package bank;

import bank.models.Role;
import bank.models.roles.Admin;
import bank.models.roles.ThirdParty;
import bank.repositories.AdminRepository;
import bank.repositories.RoleRepository;
import bank.repositories.ThirdPartyRepository;
import bank.services.AdminService;
import bank.services.ThirdPartyService;
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
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private ThirdPartyService thirdPartyService;

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
        thirdPartyRepository.save(tester);
    }

    @AfterEach
    public void tearDown() {
        thirdPartyRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void addNewThirdPartyTest() {
        ThirdParty checker = thirdPartyService.findByUsername(tester.getUsername()).get();
        System.out.println("flag2");
        assertEquals(checker.getUsername(), "Tester");
        System.out.println("flag3");
        assertTrue(passwordEncoder.matches("$2a$12$p8qaYHmtyYnMgyMja2vsbe8K/vXs9NmjFaqfjBQ6Osro68ygS2ogW", checker.getPassword()));
         System.out.println("flag5");
        assertEquals(checker.getRole().getRole(), THIRD_PARTY);
    }
}