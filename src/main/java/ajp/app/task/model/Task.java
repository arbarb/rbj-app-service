package ajp.app.task.model;

import ajp.app.firebase.FirestoreEntity;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.Data;

import java.util.Date;

@Data
public class Task implements FirestoreEntity {

    public static final String PATH = "tasks";

    @DocumentId
    private String id;

    private String title;
    private String description;
    private String status;
    private String assignedTo;
    private Date dueDate;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;
}
