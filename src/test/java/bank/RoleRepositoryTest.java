package bank;

import bank.enums.Roles;
import bank.models.Role;
import bank.repositories.RoleRepository;
import bank.services.RoleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static bank.enums.Roles.*;
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
        List<Roles> roles = List.of(ADMIN, ACCOUNT_HOLDER, THIRD_PARTY);
        for (Roles role : roles) {
            if (!roleService.findByRole(role).isPresent()) {
                tester = new Role();
                tester.setRole(role);
                roleRepository.save(tester);
            }
        }
    }

    @AfterEach
    public void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    void addRoleTest() {
        Role checker = roleService.findByRole(ADMIN).get();
        assertEquals(ADMIN, checker.getRole());
    }
}
