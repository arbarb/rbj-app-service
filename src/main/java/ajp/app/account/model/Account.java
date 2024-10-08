package ajp.app.account.model;

import ajp.app.firebase.FirestoreEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Account implements FirestoreEntity {

    public static final String PATH = "account";

    @DocumentId
    private String id;

    private String userUid;
    private String username;
    private String email;

    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String status;

    private List<String> roles;

    private String createdBy;
    private String updatedBy;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @ServerTimestamp
    private Date createdAt;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @ServerTimestamp
    private Date updatedAt;

}
