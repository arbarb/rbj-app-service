package ajp.app.task.service;

import ajp.app.account.model.Account;
import ajp.app.account.service.AccountService;
import ajp.app.authentication.service.AuthenticationService;
import ajp.app.task.converter.TaskConverter;
import ajp.app.task.model.CreateTaskRequest;
import ajp.app.task.model.SearchTaskRequest;
import ajp.app.task.model.Task;
import ajp.app.task.model.TaskResponse;
import ajp.app.task.repository.TaskRepository;
import com.google.cloud.firestore.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static ajp.app.common.DateUtil.toDate;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;

    private final TaskConverter taskConverter;

    private final AccountService accountService;

    private final AuthenticationService authenticationService;

    public Mono<TaskResponse> getTask(String id) {
        return repository.get(id).map(this::mapResponse);
    }

    public Flux<TaskResponse> searchTask(SearchTaskRequest request) {
        List<Filter> filters = new ArrayList<>();

        String status = trimToNull(request.getStatus());
        if (status != null) {
            filters.add(Filter.equalTo("status", status.toLowerCase()));
        }

        String title = trimToNull(request.getTitle());
        if (title != null) {
            filters.add(Filter.equalTo("title", title));
        }

        String dueDate = trimToNull(request.getDueDate());
        String onOrBeforeDueDate = trimToNull(request.getOnOrBeforeDueDate());
        if (dueDate != null) {
            filters.add(Filter.equalTo("dueDate", toDate(dueDate)));
        } else if (onOrBeforeDueDate != null) {
            filters.add(Filter.lessThanOrEqualTo("dueDate", toDate(onOrBeforeDueDate)));
        }

        if (filters.size() > 0) {
            Filter filter = Filter.and(filters.toArray(Filter[]::new));
            Flux<Task> tasks = repository.find(filter);
            return tasks.map(this::mapResponse);
        }

        return Flux.empty();
    }

    public Flux<TaskResponse> getTaskList(String status) {

        Filter filter = getSearchFilter(status);
        Flux<Task> tasks = repository.find(filter);
        return tasks.map(this::mapResponse);
    }

    private Filter getSearchFilter(String status) {
        List<Filter> filters = new ArrayList<>();

        Account account = authenticationService.getCurrentUserAccount();
        log.info("account={}", account);

        filters.add(Filter.equalTo(TaskRepository.ASSIGNED_TO, account.getUserUid()));

        if (trimToNull(status) != null) {
            filters.add(Filter.equalTo(TaskRepository.STATUS, trimToEmpty(status).toLowerCase()));
        }

        if (filters.size() == 1) {
            return filters.get(0);
        }

        return Filter.and(filters.toArray(Filter[]::new));
    }

    public Mono<TaskResponse> createTask(CreateTaskRequest request) {
        Mono<Account> account = accountService.getOrCreateLoginAccount();


        Task task = taskConverter.toTask(request);
        Mono<Task> response = repository.save(task);
        return response.map(this::mapResponse);
    }

    public Mono<Integer> deleteInactive() {
        return repository.deleteInActiveOrPastDue();
    }

    public Mono<Void> deleteTask(String id) {
        return repository.remove(id);
    }

    private TaskResponse mapResponse(Task task) {
        return task == null ? null : taskConverter.toTaskResponse(task);
    }

}
