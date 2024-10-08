package ajp.app.authentication.controller;

import ajp.app.authentication.model.LoginRequest;
import ajp.app.authentication.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest login) {
        return null;
    }

}
