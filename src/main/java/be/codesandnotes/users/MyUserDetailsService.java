package be.codesandnotes.users;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

public class MyUserDetailsService implements UserDetailsService {

    private PasswordEncoder passwordEncoder;

    public MyUserDetailsService() {
    }

    public MyUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!username.equals("user")) {
            throw new UsernameNotFoundException("not found");
        }

        return new User("user", passwordEncoder.encode("password"), Collections.singleton(new SimpleGrantedAuthority("USER")));
    }
}
