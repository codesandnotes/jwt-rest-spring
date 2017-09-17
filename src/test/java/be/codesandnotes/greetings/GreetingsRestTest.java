package be.codesandnotes.greetings;

import be.codesandnotes.TestRestClient;
import be.codesandnotes.IntegrationTestsApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.UUID;

import static org.junit.Assert.*;

@SpringBootTest(
        classes = IntegrationTestsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@RunWith(SpringRunner.class)
public class GreetingsRestTest {

    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";
    private static final String CSRF_TOKEN = UUID.randomUUID().toString();
    private static final Object BODY = null;

    @Resource
    private TestRestTemplate restTemplate;

    private TestRestClient restClient;

    @Before
    public void init() {
        restClient = new TestRestClient(restTemplate);
    }

    @Test
    public void returnUnsecuredGreetings() {

        ResponseEntity<GreetingWebObject> response
                = restTemplate.getForEntity("/rest/unsecure/greetings", GreetingWebObject.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Greetings and salutations!", response.getBody().getMessage());
    }

    @Test
    public void returnSecureGreetings() {

        TestRestClient.Credentials credentials = restClient.login(USERNAME, PASSWORD);
        ResponseEntity<GreetingWebObject> response = restClient.get("/rest/secure/greetings", credentials, GreetingWebObject.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Greetings and salutations, user!", response.getBody().getMessage());
    }

    @Test
    public void protectAccessToSecureGreetingsWhenUserIsNotLogged() {

        ResponseEntity<GreetingWebObject> response = restTemplate.getForEntity("/rest/secure/greetings", GreetingWebObject.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void protectAccessToSecurePOSTGreetingWhenUserIsNotLogged() {

        TestRestClient.Credentials credentials = new TestRestClient.Credentials(null);
        ResponseEntity<GreetingWebObject> response = restClient.get("/rest/secure/greetings", credentials, GreetingWebObject.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void protectAccessToSecurePOSTGreetingWhenCSRFTokensAreNotSent() {

        TestRestClient.Credentials credentials = restClient.login(USERNAME, PASSWORD);
        ResponseEntity<GreetingWebObject> response = restClient.post("/rest/secure/greetings", credentials,
                BODY, GreetingWebObject.class, null);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void returnSecurePOSTGreetings() {

        TestRestClient.Credentials credentials = restClient.login(USERNAME, PASSWORD);
        ResponseEntity<GreetingWebObject> response = restClient.post("/rest/secure/greetings", credentials,
                BODY, GreetingWebObject.class, CSRF_TOKEN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Greetings and POST-salutations, user!", response.getBody().getMessage());
    }
}
