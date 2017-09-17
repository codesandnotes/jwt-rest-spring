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
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfFilter;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String CSRF_COOKIE = "CSRF-TOKEN";
    public static final String CSRF_HEADER = "X-CSRF-TOKEN";

    static final long TOKEN_LIFETIME = 604_800_000;
    static final String TOKEN_PREFIX = "Bearer ";
    static final String TOKEN_SECRET = Base64.getEncoder().encodeToString("ThisIsOurSecretKeyToSignOurTokens".getBytes());

    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint;

    private StatelessCsrfFilter statelessCsrfFilter = new StatelessCsrfFilter();

    @Resource
    private MyUserDetailsService myUserDetailsService;

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() throws NoSuchAlgorithmException {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }

    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .addFilterBefore(statelessCsrfFilter, CsrfFilter.class);

        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

        http
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .addFilter(new TokenBasedAuthenticationFilter(authenticationManager()))
                .addFilter(new TokenBasedAuthorizationFilter(authenticationManager()));

        http.authorizeRequests()
                .antMatchers("/rest/unsecure/**").permitAll()
                .antMatchers("/**").authenticated();
    }
}
