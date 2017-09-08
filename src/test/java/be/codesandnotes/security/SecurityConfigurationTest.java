package be.codesandnotes.security;

import be.codesandnotes.IntegrationTestsApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
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
    public void authenticateAnExistingUser() {
        String username = "user";
        String password = "password";

        List<String> cookies = rest.execute(
                "/login",
                POST,
                request -> {
                    OutputStream body = request.getBody();
                    body.write(("username=" + username + "&password=" + password).getBytes());
                    body.flush();
                    body.close();

                }, response -> {
                    HttpHeaders loginHeaders = response.getHeaders();
                    return loginHeaders.get(HttpHeaders.SET_COOKIE);
                }
        );

        assertEquals(1, cookies.size());
        assertTrue(cookies.get(0).contains("JSESSIONID="));
    }
}
