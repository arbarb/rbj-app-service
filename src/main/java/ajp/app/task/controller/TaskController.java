package ajp.app.task.controller;

import ajp.app.common.model.ApiResponse;
import ajp.app.task.model.CreateTaskRequest;
import ajp.app.task.model.SearchTaskRequest;
import ajp.app.task.model.TaskResponse;
import ajp.app.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping
    public Mono<ApiResponse<List<TaskResponse>>> getAllTask(@RequestParam(name = "status", required = false) String status) {
        return service.getTaskList(status).switchIfEmpty(Flux.empty())
                .collectList().map(ApiResponse::from);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("/search")
    public Mono<ApiResponse<List<TaskResponse>>> search(@RequestParam(name = "title", required = false) String title,
                                                        @RequestParam(name = "status", required = false) String status,
                                                        @RequestParam(name = "dueDate", required = false) String dueDate) {
        SearchTaskRequest request = new SearchTaskRequest();
        request.setTitle(title);
        request.setStatus(status);
        request.setDueDate(dueDate);
        return service.searchTask(request).switchIfEmpty(Flux.just())
                .collectList().map(ApiResponse::from);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<TaskResponse>> getTask(@PathVariable String id) {
        return service.getTask(id)
                .map(ApiResponse::from)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND::Task not found")));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<ApiResponse<TaskResponse>> createTask(@RequestBody CreateTaskRequest request) {
        return service.createTask(request).map(ApiResponse::from);
    }

    @PostMapping("/clear-inactive")
    public Mono<ApiResponse<Integer>> clearInactive() {
        return service.deleteInactive().map(ApiResponse::from);
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> deleteTask(@PathVariable String id) {
        return service.deleteTask(id).map(ApiResponse::from);
    }

}
