package bank;

import bank.enums.Roles;
import bank.models.Role;
import bank.models.roles.Admin;
import bank.repositories.AdminRepository;
import bank.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BankApplication {

	@Autowired
	AdminRepository adminRepository;

	@Autowired
	RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

	/** This will be moved into a controller */

	/*
	public void createNewAdmin(Admin admin, Roles roles) {
		Role role = new Role();
		role.setRole(roles);
		roleRepository.save(role);
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		admin.setRole(role);
		adminRepository.save(admin);
	}*/

}
