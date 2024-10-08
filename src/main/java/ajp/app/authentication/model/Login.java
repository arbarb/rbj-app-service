package ajp.app.authentication.model;

import lombok.Data;

import java.util.Date;

@Data
public class Login {
    private String userId;
    private String email;
    private Date expirationDate;
}
