package bank;

import bank.models.Role;
import bank.models.roles.Admin;
import bank.repositories.AdminRepository;
import bank.repositories.RoleRepository;
import bank.services.AdminService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static bank.enums.Roles.ADMIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleRepository roleRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    Admin tester;
    Role auxRole;

    @BeforeEach
    public void setUp() {
        tester = new Admin();
        tester.setUsername("Tester");
        tester.setPassword(passwordEncoder.encode("adminpassword"));
        auxRole = new Role();
        auxRole.setRole(ADMIN);
        tester.setRole(auxRole);
        roleRepository.save(auxRole);
        adminRepository.save(tester);
    }

    @AfterEach
    public void tearDown() {
        adminRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void addNewAdminTest() {
        Admin checker = adminService.findByUsername(tester.getUsername()).get();
        assertEquals(checker.getUsername(), "Tester");
        assertTrue(passwordEncoder.matches("adminpassword", checker.getPassword()));
        assertNotEquals(checker.getPassword(), passwordEncoder.encode("adminpassword"));
        assertEquals(checker.getRole().getRole(), ADMIN);
    }
}
