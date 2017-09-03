package be.codesandnotes.greetings;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/rest")
public class GreetingsRest {

    // Make sure you don't use "consumes" in the RequestMapping, otherwise one gets 415 codes
    @RequestMapping(path = "/unsecure/greetings", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<GreetingWebObject> unsecuredGreetings() {
        return ResponseEntity.ok(new GreetingWebObject("Greetings and salutations!"));
    }

    ResponseEntity<GreetingWebObject> securedGreetings() {
        return null;
    }

    ResponseEntity<GreetingWebObject> securedGreetings(String postGreetingsMessage) {
        return null;
    }
}
