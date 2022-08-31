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
    Admin testerA;
    Role auxRoleA;
    Admin checkerA;

    @BeforeEach
    public void setUp() {
        auxRoleA = new Role();
        auxRoleA.setRole(ADMIN);
        roleRepository.save(auxRoleA);
        testerA = new Admin();
        testerA.setUsername("TesterA");
        testerA.setPassword(passwordEncoder.encode("adminpassword"));
        testerA.setRole(auxRoleA);
        adminRepository.save(testerA);
        checkerA = adminService.findByUsername(testerA.getUsername()).get();
    }

    @AfterEach
    public void tearDown() {
        adminRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void addNewAdminTest() {
        assertEquals(checkerA.getUsername(), "TesterA");
        assertTrue(passwordEncoder.matches("adminpassword", checkerA.getPassword()));
        assertNotEquals(checkerA.getPassword(), passwordEncoder.encode("adminpassword"));
        assertEquals(checkerA.getRole().getRole(), ADMIN);
    }

    @Test
    void deleteAdminTest(){
        assertThrows(Exception.class, () -> { roleRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { adminRepository.deleteAll(); } );
        assertDoesNotThrow(() -> { roleRepository.deleteAll(); } );
    }
}
