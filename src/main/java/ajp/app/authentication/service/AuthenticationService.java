package ajp.app.authentication.service;

import ajp.app.authentication.model.Login;
import ajp.app.authentication.model.LoginRequest;
import ajp.app.authentication.model.UserData;
import ajp.app.common.CommonUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

import static ajp.app.common.DateUtil.toDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    public static final String USER_DATA = "userData";

    private final FirebaseAuth firebaseAuth;

    public String login(LoginRequest request) {
        try {
            FirebaseToken token = firebaseAuth.verifyIdToken(request.getIdToken());
            token.getEmail();
            UserRecord user = firebaseAuth.getUserByEmail(request.getEmail());
        } catch (Exception e) {
            log.error("login error", e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
        return "";
    }

    private Optional<Jwt> getCredentials() {
        return getAuthentication().map(jwt -> (Jwt) jwt.getCredentials());
    }

    public Optional<Login> getLogin() {
        return getCredentials().map(credentials -> {
            Login login = new Login();
            login.setUserId(credentials.getClaimAsString("user_id"));
            login.setEmail(credentials.getClaimAsString("email"));
            login.setExpirationDate(toDate(credentials.getClaimAsInstant("exp")));
            return login;
        });
    }

    public @NotNull Login getCurrentLogin() {
        return getLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login not found"));
    }

    public UserData getUserData() {
        try {
            @NotNull Login login = getCurrentLogin();
            UserRecord userRecord = firebaseAuth.getUser(login.getUserId());
            Map<String, Object> claims = userRecord.getCustomClaims();
            log.info("userRecord custom claims = {}", claims);
            return CommonUtil.convertValue(claims.get(USER_DATA), UserData.class);
        } catch (ResponseStatusException e) {
            log.error("getCurrentUserRecord error", e);
            throw e;
        } catch (Exception e) {
            log.error("getCurrentUserRecord error", e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    public void setUserData(String userId, UserData userData) {
        try {
            firebaseAuth.setCustomUserClaims(userId, Map.of(USER_DATA, userData));
        } catch (FirebaseAuthException e) {
            log.error("", e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    private Optional<JwtAuthenticationToken> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(context -> (JwtAuthenticationToken) context.getAuthentication());
    }

}
