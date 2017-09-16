package be.codesandnotes.security;

import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static be.codesandnotes.security.SecurityConfiguration.*;

public class TokenBasedAuthorizationFilter extends BasicAuthenticationFilter {

    TokenBasedAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String authorizationToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationToken != null && authorizationToken.startsWith(TOKEN_PREFIX)) {
            authorizationToken = authorizationToken.replaceFirst(TOKEN_PREFIX, "");

            String username = Jwts.parser()
                    .setSigningKey(TOKEN_SECRET)
                    .parseClaimsJws(authorizationToken)
                    .getBody()
                    .getSubject();

            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList()));
        }

        chain.doFilter(request, response);
    }
}
