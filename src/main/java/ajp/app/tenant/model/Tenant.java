package ajp.app.tenant.model;

import ajp.app.firebase.FirestoreEntity;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tenant implements FirestoreEntity {

    @DocumentId
    private String id;

    private String firstName;
    private String lastName;
    private String middleName;
    private Date birthDate;

    private String idType;
    private String idNumber;

    private String username;
    private String email;
    private String contact;

    private String status;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;

}
