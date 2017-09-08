package be.codesandnotes.security;

import be.codesandnotes.users.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final int PASSWORD_ENCODER_STRENGTH = 10;

    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint;

    private AuthenticationFailureHandler authenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();

    @Resource
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Resource
    private LogoutSuccessHandler logoutSuccessHandler;

    @Resource
    private MyUserDetailsService myUserDetailsService;

    // Should we really disable the defaults?
//    public SecurityConfiguration() {
//        super(true);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() throws NoSuchAlgorithmException {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }

    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.formLogin().successHandler(authenticationSuccessHandler);
        http.formLogin().failureHandler(authenticationFailureHandler);

        http.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);

        http.authorizeRequests()
                .antMatchers("/rest/unsecure/**").permitAll()
                .antMatchers("/**").authenticated();

//        http.addFilterBefore(new StatelessAuthenticationFilter(tokenAuthenticationService()), UsernamePasswordAuthenticationFilter.class);
    }
}
