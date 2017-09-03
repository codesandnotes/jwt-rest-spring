package be.codesandnotes.greetings;

import be.codesandnotes.IntegrationTestsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@SpringBootTest(
        classes = IntegrationTestsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@RunWith(SpringRunner.class)
public class GreetingsRestTest {

    @Resource
    private TestRestTemplate rest;

    @Test
    public void sendUnsecuredGreetings() {

        ResponseEntity response = rest.getForEntity("/rest/unsecure/greetings", GreetingWebObject.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        GreetingWebObject responseBody = (GreetingWebObject) response.getBody();
        assertEquals("Greetings and salutations!", responseBody.getMessage());
    }
}
