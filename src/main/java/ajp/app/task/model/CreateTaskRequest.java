package ajp.app.task.model;

import lombok.Data;

@Data
public class CreateTaskRequest {
    private String title;
    private String description;
    private String status;
    private String dueDate;
}
