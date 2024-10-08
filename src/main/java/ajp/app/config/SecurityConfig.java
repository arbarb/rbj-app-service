package ajp.app.config;

import ajp.app.account.model.Account;
import ajp.app.account.service.AccountService;
import ajp.app.authentication.model.UserData;
import ajp.app.authentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AccountService accountService;

    private final AuthenticationService authenticationService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("securityFilterChain");
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(antMatcher("/health")).permitAll();
                    auth.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oAuth -> {
                    oAuth.accessDeniedHandler((request, response, accessDeniedException) -> {
                        log.info("ACCESS DENIED HANDLER");
                    });
                    oAuth.jwt(jwt -> {
                        jwt.jwtAuthenticationConverter(authenticationConverter());
                    });
                })
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling.accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Restricted Content\"");
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
                }))
                .exceptionHandling(handling -> handling.authenticationEntryPoint((request, response, authException) -> {
                    response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Restricted Content\"");
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
                }))
                .build();
    }

    private JwtAuthenticationConverter authenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String userId = jwt.getClaim("user_id");
            String email = jwt.getClaim("email");
            return accountService.getOrCreateAccount(userId, email)
                    .map(this::mapUserData)
                    .doOnSuccess(userData -> authenticationService.setUserData(userId, userData))
                    .map(UserData::getAuthorities)
                    .block();
        });
        return converter;
    }

    private UserData mapUserData(Account account) {
        UserData userData = new UserData();

        userData.setUsername(account.getUsername());
        userData.setUserId(account.getUserUid());
        userData.setEmail(account.getEmail());

        userData.setFirstName(account.getFirstName());
        userData.setMiddleName(account.getMiddleName());
        userData.setLastName(account.getLastName());
        userData.setGender(account.getGender());
        userData.setStatus(account.getStatus());

        userData.setAuthorities(Optional.ofNullable(account.getRoles()).stream()
                .flatMap(List::stream)
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                .toList());
        return userData;
    }


}
