package ajp.app.tenant.repository;

import ajp.app.firebase.FirestoreRepository;
import ajp.app.tenant.model.Tenant;
import com.google.cloud.firestore.Firestore;

public class TenantRepository extends FirestoreRepository<Tenant> {

    private static final String PATH  = "tenants";

    public TenantRepository(Firestore store) {
        super(Tenant.class, PATH, store);
    }


}
