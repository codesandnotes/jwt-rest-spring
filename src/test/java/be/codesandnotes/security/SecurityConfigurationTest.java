package be.codesandnotes.security;

import be.codesandnotes.IntegrationTestsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;

@SpringBootTest(
        classes = IntegrationTestsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@RunWith(SpringRunner.class)
public class SecurityConfigurationTest {

    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";
    private static final String CSRF_TOKEN = UUID.randomUUID().toString();
    private static final String DIFFERENT_CSRF_TOKEN = UUID.randomUUID().toString();

    @Resource
    private TestRestTemplate rest;

    @Test
    public void authenticateAnExistingUser() throws IOException {

        ClientHttpResponse clientHttpResponse = login(USERNAME, PASSWORD, CSRF_TOKEN);

        assertNotNull(clientHttpResponse);
        assertEquals(HttpStatus.OK, clientHttpResponse.getStatusCode());

        assertEquals(true, clientHttpResponse.getHeaders().containsKey(HttpHeaders.AUTHORIZATION));
        List<String> authorizationTokens = clientHttpResponse.getHeaders().get(HttpHeaders.AUTHORIZATION);
        assertEquals(1, authorizationTokens.size());

        String authorizationToken = authorizationTokens.get(0);
        assertEquals(true, authorizationToken.startsWith("Bearer "));
    }

    @Test
    public void blockAuthenticationWhenCSRFTokensAreAbsent() throws IOException {

        ClientHttpResponse clientHttpResponse = login(USERNAME, PASSWORD);

        assertNotNull(clientHttpResponse);
        assertEquals(HttpStatus.FORBIDDEN, clientHttpResponse.getStatusCode());
    }

    @Test
    public void blockAuthenticationWhenCSRFTokenIsAbsentFromHeader() throws IOException {

        ClientHttpResponse clientHttpResponse = login(USERNAME, PASSWORD, null, CSRF_TOKEN);

        assertNotNull(clientHttpResponse);
        assertEquals(HttpStatus.FORBIDDEN, clientHttpResponse.getStatusCode());
    }

    @Test
    public void blockAuthenticationWhenCSRFTokenIsAbsentFromCookie() throws IOException {

        ClientHttpResponse clientHttpResponse = login(USERNAME, PASSWORD, CSRF_TOKEN, null);

        assertNotNull(clientHttpResponse);
        assertEquals(HttpStatus.FORBIDDEN, clientHttpResponse.getStatusCode());
    }

    @Test
    public void blockAuthenticationWhenCSRFTokensDoNotMatch() throws IOException {

        ClientHttpResponse clientHttpResponse = login(USERNAME, PASSWORD, CSRF_TOKEN, DIFFERENT_CSRF_TOKEN);

        assertNotNull(clientHttpResponse);
        assertEquals(HttpStatus.FORBIDDEN, clientHttpResponse.getStatusCode());
    }

    private ClientHttpResponse login(String username, String password) {
        return rest.execute(
                "/login",
                POST,
                request -> {
                    OutputStream body = request.getBody();
                    body.write(("username=" + username + "&password=" + password).getBytes());
                    body.flush();
                    body.close();
                },
                response -> response
        );
    }

    private ClientHttpResponse login(String username, String password, String csrfToken) {
        return login(username, password, csrfToken, csrfToken);
    }

    private ClientHttpResponse login(String username, String password, String csrfHeaderToken, String csrfCookieToken) {
        return rest.execute(
                "/login",
                POST,
                request -> {
                    // Body
                    OutputStream body = request.getBody();
                    body.write(("username=" + username + "&password=" + password).getBytes());
                    body.flush();
                    body.close();

                    // Headers
                    HttpHeaders headers = request.getHeaders();
                    if (csrfHeaderToken != null) {
                        headers.set(HttpHeaders.COOKIE, SecurityConfiguration.CSRF_COOKIE + "=" + csrfHeaderToken);
                    }
                    if (csrfCookieToken != null) {
                        headers.set(SecurityConfiguration.CSRF_HEADER, csrfCookieToken);
                    }
                },
                response -> response
        );
    }
}
