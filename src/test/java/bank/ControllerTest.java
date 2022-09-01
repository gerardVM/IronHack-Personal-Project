package bank;

import bank.models.accounts.Checking;
import bank.models.Role;
import bank.models.roles.AccountHolder;
// import bank.repositories.AccountHolderRepository;
import bank.repositories.CheckingRepository;
import bank.repositories.RoleRepository;
import bank.repositories.UserRepository;
import bank.services.CheckingService;
import bank.services.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static bank.enums.Roles.ACCOUNT_HOLDER;
import static bank.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private CheckingService checkingService;
    @Autowired
    // private AccountHolderRepository accountHolderRepository;
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    Checking testerC;
    AccountHolder auxUserC;
    Role auxRoleC;
    Checking checkerC;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Product product;
    // Gson gson = new Gson();

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper.findAndRegisterModules();
        if (roleService.findByRole(ACCOUNT_HOLDER).isPresent()) {
            auxRoleC = roleService.findByRole(ACCOUNT_HOLDER).get();
        } else {
            auxRoleC = new Role();
            auxRoleC.setRole(ACCOUNT_HOLDER);
            roleRepository.save(auxRoleC);
        }
        auxUserC = new AccountHolder();
        auxUserC.setUsername("AuxUserC");
        auxUserC.setPassword(passwordEncoder.encode("password"));
        auxUserC.setRole(auxRoleC);
        auxUserC.setBirthDate(LocalDate.of(1990, 1, 1));
        userRepository.save(auxUserC);
        testerC = new Checking();
        testerC.setBalance(BigDecimal.valueOf(250));
        testerC.setPrimaryOwner(auxUserC);
        testerC.setSecretKey(passwordEncoder.encode("1234"));
        testerC.setAccountStatus(ACTIVE);
        checkingRepository.save(testerC);
        checkerC = checkingRepository.findByPrimaryOwner(testerC.getPrimaryOwner()).get();
    }

    @AfterEach
    void tearDown() {
        checkingRepository.deleteById(checkerC.getId());
        userRepository.deleteById(checkerC.getPrimaryOwner().getId());
        roleRepository.deleteById(checkerC.getPrimaryOwner().getRole().getId());
    }

    // Como se añade la autenticación?
    @Test
    void create_test() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/balance").param("id", checkerC.getId().toString()))

                .andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("250.00"));
        // Lacking authentication testing

        // ProductDTO product = new ProductDTO("New product", 69, 1L);
        // String body = gson.toJson(product);
        // List<Object> list = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Object>>(){});
        //assertTrue(list.contains("250.00"));




                       // .content(body)
                       // .contentType(MediaType.APPLICATION_JSON))
                // .andExpect(status().isCreated()).andReturn();

        //assertTrue(mvcResult.getResponse().getContentAsString().contains("New product"));
    }
}
