package be.codesandnotes.greetings;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/rest")
public class GreetingsRest {

    // Make sure you don't use "consumes" in the RequestMapping, otherwise one gets 415 codes
    @RequestMapping(path = "/unsecure/greetings", method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<GreetingWebObject> unsecuredGreetings() {
        return ResponseEntity.ok(new GreetingWebObject("Greetings and salutations!"));
    }

    @RequestMapping(path = "/secure/greetings", method = GET, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<GreetingWebObject> securedGreetings(Principal principal) {
        return ResponseEntity.ok(new GreetingWebObject("Greetings and salutations, " + principal.getName() + "!"));
    }

    @RequestMapping(path = "/secure/greetings", method = POST, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<GreetingWebObject> securedGreetingsPost(Principal principal) {
        return ResponseEntity.ok(new GreetingWebObject("Greetings and POST-salutations, " + principal.getName() + "!"));
    }
}
