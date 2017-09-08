package be.codesandnotes.users;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@Configuration
public class UsersConfiguration {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Bean
    public MyUserDetailsService usersDAO() {
        return new MyUserDetailsService(passwordEncoder);
    }
}
