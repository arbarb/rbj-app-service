package ajp.app.authentication.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class UserData implements UserDetails {

    private String userId;
    private String email;

    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String status;

    private String username;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private List<GrantedAuthority> authorities;

    public String getPassword() {
        return null;
    }
}
