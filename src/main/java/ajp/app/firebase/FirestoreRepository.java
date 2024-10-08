package ajp.app.firebase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static ajp.app.common.DateUtil.now;

@Slf4j
public class FirestoreRepository<T extends FirestoreEntity> {

    private final Class<T> cls;

    private final String path;

    private final Firestore store;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {

    };

    public FirestoreRepository(Class<T> cls, String path, Firestore store) {
        this.cls = cls;
        this.path = path;
        this.store = store;
    }

    protected CollectionReference db() {
        return this.store.collection(path);
    }

    public Flux<T> find(Filter filter) {
        return Flux.create(sink -> {
            try {
                ApiFuture<QuerySnapshot> future = db().where(filter).get();
                List<QueryDocumentSnapshot> docs = future.get().getDocuments();
                docs.forEach(doc -> sink.next(doc.toObject(cls)));
                sink.complete();
            } catch (Exception e) {
                log.error("Error on find", e);
                sink.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
            }
        });
    }

    public Flux<T> getAll() {
        return Flux.create(sink -> {
            try {
                ApiFuture<QuerySnapshot> future = db().get();
                List<QueryDocumentSnapshot> docs = future.get().getDocuments();
                docs.forEach(doc -> sink.next(doc.toObject(cls)));
                sink.complete();
            } catch (Exception e) {
                log.error("Error on get all", e);
                sink.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
            }
        });
    }

    public Mono<T> get(String id) {
        return Mono.create(sink -> {
            try {
                ApiFuture<DocumentSnapshot> future = db().document(id).get();
                DocumentSnapshot doc = future.get();
                sink.success(doc.toObject(cls));
            } catch (Exception e) {
                log.error("Error on get", e);
                sink.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
            }
        });
    }

    public Mono<T> save(T data) {
        return Mono.create(sink -> {
            try {
                DocumentReference docRef = db().document();
                WriteResult writeResult = docRef.set(data).get();

                Date updateTime = writeResult.getUpdateTime().toDate();
                data.setId(docRef.getId());
                data.setCreatedAt(updateTime);
                data.setUpdatedAt(updateTime);
                sink.success(data);
            } catch (Exception e) {
                log.error("Error on save", e);
                sink.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
            }
        });
    }

    public Mono<T> save(String id, T data) {
        return Mono.create(sink -> {
            try {
                data.setId(id);
                data.setUpdatedAt(now());

                DocumentReference docRef = db().document(id);
                docRef.set(data).get();

                sink.success(data);
            } catch (Exception e) {
                log.error("Error on save", e);
                sink.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
            }
        });
    }

    public Mono<T> update(String id, T data) {
        return Mono.create(sink -> {
            try {
                data.setId(id);
                data.setUpdatedAt(now());

                DocumentReference docRef = db().document(id);
                docRef.set(data, SetOptions.merge()).get();

                sink.success(data);
            } catch (Exception e) {
                log.error("Error on update", e);
                sink.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
            }
        });
    }

    public Mono<T> update(String id, Map<String, Object> fields) {
        return Mono.create(sink -> {
            try {
                DocumentReference docRef = db().document(id);
                docRef.set(fields, SetOptions.merge()).get();
                DocumentSnapshot doc = docRef.get().get();
                sink.success(doc.toObject(cls));
            } catch (Exception e) {
                log.error("Error on update", e);
                sink.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
            }
        });
    }

    public Mono<Void> remove(String id) {
        return Mono.create(sink -> {
            try {
                db().document(id).delete().get();
                sink.success();
            } catch (Exception e) {
                log.error("Error on remove", e);
                sink.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
            }
        });
    }

    public Mono<Integer> saveAll(List<T> dataList) {
        return Mono.create(sink -> {
            try {
                WriteBatch batch = store.batch();
                dataList.forEach(data -> batch.create(db().document(), data));
                batch.commit();
                sink.success(batch.getMutationsSize());
            } catch (ResponseStatusException e) {
                throw e;
            } catch (Exception e) {
                log.error("batchUpdate", e);
                sink.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
            }
        });
    }

    public Mono<Integer> updateAll(List<T> dataList) {
        return Mono.create(sink -> {
            try {
                WriteBatch batch = store.batch();
                Date currentDate = now();
                dataList.forEach(data -> {
                    String id = data.getId();
                    Map<String, Object> mappedData = convertValue(data);
                    mappedData.remove("id");
                    mappedData.remove("createdAt");
                    mappedData.put("updatedAt", currentDate);
                    batch.update(db().document(id), mappedData);
                });
                batch.commit();
                sink.success(batch.getMutationsSize());
            } catch (ResponseStatusException e) {
                throw e;
            } catch (Exception e) {
                log.error("batchUpdate", e);
                sink.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
            }
        });
    }

    public Mono<Integer> removeAll(List<T> dataList) {
        return Mono.create(sink -> {
            try {
                WriteBatch batch = store.batch();
                Date currentDate = now();
                dataList.forEach(data -> {
                    String id = data.getId();
                    Map<String, Object> mappedData = convertValue(data);
                    mappedData.remove("id");
                    mappedData.remove("createdAt");
                    mappedData.put("updatedAt", currentDate);
                    batch.delete(db().document(id));
                });
                batch.commit();
                sink.success(batch.getMutationsSize());
            } catch (ResponseStatusException e) {
                throw e;
            } catch (Exception e) {
                log.error("batchUpdate", e);
                sink.error(new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
            }
        });
    }

    private Map<String, Object> convertValue(T data) {
        try {
            return mapper.convertValue(data, MAP_TYPE_REFERENCE);
        } catch (Exception e) {
            String message = String.format("Unable to convert data %s. %s", data, e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, message);
        }
    }

}