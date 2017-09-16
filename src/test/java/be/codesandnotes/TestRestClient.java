package be.codesandnotes;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.OutputStream;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

public class TestRestClient {

    private TestRestTemplate rest;

    public TestRestClient(TestRestTemplate testRestTemplate) {
        this.rest = testRestTemplate;
    }

    public <T> ResponseEntity<T> get(String restPath, Credentials credentials, Class<T> responseType) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, credentials.token);

        return rest.exchange(restPath, GET, new HttpEntity<>(httpHeaders), responseType);
    }

    public Credentials login(String username, String password) {

        return rest.execute(
                "/login",
                POST,
                request -> {
                    OutputStream body = request.getBody();
                    body.write(("username=" + username + "&password=" + password).getBytes());
                    body.flush();
                    body.close();

                }, response -> {
                    Credentials credentials = null;

                    List<String> authorizationTokens = response.getHeaders().get(HttpHeaders.AUTHORIZATION);
                    if (authorizationTokens != null && authorizationTokens.size() > 0) {
                        credentials = new Credentials(authorizationTokens.get(0));
                    }

                    return credentials;
                }
        );
    }

    public static class Credentials {
        public String token;
        Credentials(String token) {
            this.token = token;
        }
    }
}
