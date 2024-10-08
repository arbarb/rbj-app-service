package ajp.app.authentication.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String idToken;
    private String username;
    private String email;
    private String password;
}
