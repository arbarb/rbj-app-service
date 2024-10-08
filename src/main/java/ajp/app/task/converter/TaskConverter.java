package ajp.app.task.converter;

import ajp.app.task.model.CreateTaskRequest;
import ajp.app.task.model.Task;
import ajp.app.task.model.TaskResponse;
import org.springframework.stereotype.Component;

import static ajp.app.common.DateUtil.formatDate;
import static ajp.app.common.DateUtil.toDate;
import static org.apache.commons.lang3.StringUtils.lowerCase;

@Component
public class TaskConverter {

    public Task toTask(CreateTaskRequest request) {
        var task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(lowerCase(request.getStatus()));
        task.setDueDate(toDate(request.getDueDate()));
        return task;
    }

    public TaskResponse toTaskResponse(Task task) {
        var response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setDueDate(formatDate(task.getDueDate()));
        response.setCreatedAt(formatDate(task.getCreatedAt()));
        return response;
    }


}
