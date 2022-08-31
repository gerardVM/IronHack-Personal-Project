package bank;

import bank.enums.Roles;
import bank.models.Checking;
import bank.models.Role;
import bank.models.roles.AccountHolder;
import bank.models.roles.Admin;
import bank.repositories.AdminRepository;
import bank.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

import static bank.enums.Roles.ACCOUNT_HOLDER;
import static bank.enums.Roles.ADMIN;
import static bank.enums.Status.ACTIVE;

@SpringBootApplication
public class BankApplication implements CommandLineRunner {

	@Autowired
	private Testing testing;

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Testing testing = new Testing();
		testing.addExampleData();
	}
}
