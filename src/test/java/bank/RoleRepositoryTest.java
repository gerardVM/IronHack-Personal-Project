package bank;

import bank.models.Role;
import bank.repositories.RoleRepository;
import bank.services.RoleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static bank.enums.Roles.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;

    Role tester;

    @BeforeEach
    public void setUp() {
        tester = new Role();
        tester.setRole(ADMIN);
        roleRepository.save(tester);
    }

    @AfterEach
    public void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    void addRoleTest() {
        Role checker = roleService.findByRole(tester.getRole()).get();
        assertEquals(checker.getRole(), ADMIN);
    }
}
