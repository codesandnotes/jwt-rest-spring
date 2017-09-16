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

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;

@SpringBootTest(
        classes = IntegrationTestsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@RunWith(SpringRunner.class)
public class SecurityConfigurationTest {

    @Resource
    private TestRestTemplate rest;

    @Test
    public void authenticateAnExistingUser() throws IOException {
        String username = "user";
        String password = "password";

        ClientHttpResponse clientHttpResponse = rest.execute(
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

        assertNotNull(clientHttpResponse);
        assertEquals(HttpStatus.OK, clientHttpResponse.getStatusCode());

        assertEquals(true, clientHttpResponse.getHeaders().containsKey(HttpHeaders.AUTHORIZATION));
        List<String> authorizationTokens = clientHttpResponse.getHeaders().get(HttpHeaders.AUTHORIZATION);
        assertEquals(1, authorizationTokens.size());

        String authorizationToken = authorizationTokens.get(0);
        assertEquals("success!", authorizationToken);
    }
}
