package bank.security;
import bank.services.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration  {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Bean
    PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConf) throws Exception {
        return authConf.getAuthenticationManager();
    }




    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.httpBasic();

        httpSecurity.authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/create-account-holder").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.POST, "/create-admin").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.POST, "/create-third-party").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.PATCH, "/modify-balance").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.GET, "/balance").hasAnyRole( "ADMIN")
                .mvcMatchers(HttpMethod.GET, "/balance/{username}").hasRole("ACCOUNT_HOLDER")
                .mvcMatchers(HttpMethod.PUT, "/activate").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.PUT, "/freeze").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.GET, "/show-accounts").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.GET, "/show-users").hasRole("ADMIN")
                .anyRequest().permitAll();

        httpSecurity.csrf().disable();

        return httpSecurity.build();

    }

}