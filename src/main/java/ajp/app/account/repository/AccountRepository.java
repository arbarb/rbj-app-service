package ajp.app.account.repository;

import ajp.app.account.model.Account;
import ajp.app.firebase.FirestoreRepository;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository extends FirestoreRepository<Account> {

    private final Firestore store;

    public AccountRepository(Firestore store) {
        super(Account.class, Account.PATH, store);
        this.store = store;
    }

}
