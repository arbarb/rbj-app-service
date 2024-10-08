package ajp.app.account.service;

import ajp.app.account.model.Account;
import ajp.app.account.repository.AccountRepository;
import ajp.app.authentication.model.Login;
import ajp.app.authentication.service.AuthenticationService;
import com.google.cloud.firestore.Filter;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static ajp.app.common.DateUtil.currentDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final AuthenticationService authenticationService;

    public Optional<Account> getAccount(String userId) {
        Account account = accountRepository.get(userId).block();
        return Optional.ofNullable(account);
    }

    public Mono<Account> findByEmail(String email) {
        Filter filter = Filter.equalTo("email", email);
        return accountRepository.find(filter).collectList()
                .flatMap(this::firstAccountMono);
    }

    public Mono<Account> findUserUid(String userUid) {
        Filter filter = Filter.equalTo("userUid", userUid);
        return accountRepository.find(filter).collectList()
                .flatMap(this::firstAccountMono);
    }

    public Flux<Account> findByFilter(Filter filter) {
        return accountRepository.find(filter);
    }

    private Account firstAccount(List<Account> accounts) {
        return accounts.stream().findFirst().orElse(null);
    }

    private Mono<Account> firstAccountMono(List<Account> accounts) {
        return Mono.justOrEmpty(firstAccount(accounts));
    }

    public Mono<Account> getOrCreateLoginAccount() {
        @NotNull Login login = authenticationService.getCurrentLogin();
        return getOrCreateAccount(login.getUserId(), login.getEmail());
    }

    public Mono<Account> getOrCreateAccount(String userId, String email) {
        return findUserUid(userId).switchIfEmpty(Mono.create(sink -> {
            Account account = buildAccountFromUserIdAndEmail(userId, email);
            Mono.just(account)
                    .flatMap(accountRepository::save)
                    .doOnSuccess(sink::success)
                    .doOnError(sink::error)
                    .subscribe();
        }));
    }

    private Account buildAccountFromUserIdAndEmail(String userId, String email) {
        Account account = new Account();
        account.setUserUid(userId);
        account.setEmail(email);
        account.setCreatedBy("system");
        account.setStatus("active");
        account.setRoles(List.of("user"));
        account.setCreatedAt(currentDate());
        return account;
    }

}
