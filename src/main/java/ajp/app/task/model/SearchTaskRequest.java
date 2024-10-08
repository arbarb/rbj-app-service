package ajp.app.task.model;

import lombok.Data;

@Data
public class SearchTaskRequest {
    private String title;
    private String status;
    private String dueDate;
    private String onOrBeforeDueDate;
}
