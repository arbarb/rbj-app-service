package ajp.app.task.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskResponse {

    private String id;
    private String title;
    private String description;
    private String status;
    private String dueDate;
    private String createdAt;
    private String updatedAt;

}
