package be.codesandnotes;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.*;

public class TestRestClient {

    private TestRestTemplate rest;

    public TestRestClient(TestRestTemplate testRestTemplate) {
        this.rest = testRestTemplate;
    }

    public <T> ResponseEntity<T> get(String restPath, Credentials credentials, Class<T> responseType) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.COOKIE, "JSESSIONID=" + credentials.jSessionId);

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

                    HttpHeaders loginHeaders = response.getHeaders();
                    List<String> cookies = loginHeaders.get(HttpHeaders.SET_COOKIE);

                    if (cookies != null) {
                        Map<String, String> cookiesMap = cookies.stream()
                                .collect(Collectors.toMap((cookie) -> cookie.split("=")[0], (cookie) -> cookie.split("=")[1]));

                        String jSessionId = cookiesMap.get("JSESSIONID").contains(";") ? cookiesMap.get("JSESSIONID").split(";")[0] : cookiesMap.get("JSESSIONID");

                        credentials = new Credentials(jSessionId);
                    }

                    return credentials;
                }
        );
    }

    public static class Credentials {
        public String jSessionId;

        Credentials(String jSessionId) {
            this.jSessionId = jSessionId;
        }
    }
}
