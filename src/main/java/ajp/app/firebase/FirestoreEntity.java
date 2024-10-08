package ajp.app.firebase;

import java.util.Date;

public interface FirestoreEntity {

    String getId();

    void setId(String id);

    Date getCreatedAt();

    void setCreatedAt(Date createdAt);

    Date getUpdatedAt();

    void setUpdatedAt(Date createdAt);

}
