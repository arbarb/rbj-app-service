package ajp.app.task.repository;

import ajp.app.firebase.FirestoreRepository;
import ajp.app.task.model.Task;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;

import static ajp.app.common.DateUtil.now;

@Slf4j
@Repository
public class TaskRepository extends FirestoreRepository<Task> {

    public static final String STATUS = "status";

    public static final String DUE_DATE = "dueDate";

    public static final String ASSIGNED_TO = "assignedTo";

    private static final String ACTIVE = "active";

    private final Firestore store;

    public TaskRepository(Firestore store) {
        super(Task.class, Task.PATH, store);
        this.store = store;
    }

    public Mono<Integer> deleteInActiveOrPastDue() {
        return Mono.create(sink -> {
            try {
                Filter inactive = Filter.notEqualTo(STATUS, ACTIVE);
                Filter pastDue = Filter.lessThan(DUE_DATE, now());
                ApiFuture<QuerySnapshot> query = db().where(Filter.or(inactive, pastDue)).orderBy(DUE_DATE).get();
                List<QueryDocumentSnapshot> documents = query.get().getDocuments();
                WriteBatch batch = store.batch();
                for (QueryDocumentSnapshot document : documents) {
                    batch.delete(document.getReference(), Precondition.NONE);
                }
                batch.commit();
                sink.success(batch.getMutationsSize());
            } catch (Exception e) {
                log.error("deleteInActiveOrPastDue", e);
                sink.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
            }
        });
    }

}
