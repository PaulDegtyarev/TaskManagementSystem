package TaskManagementSystem.controller;

import TaskManagementSystem.dto.serviceResponse.TaskServiceResponseModel;
import TaskManagementSystem.dto.dbo.GeneralTaskDBO;
import TaskManagementSystem.dto.dbo.StatusDBO;
import TaskManagementSystem.dto.dbo.TaskDBOToUpdateTaskByTaskId;
import TaskManagementSystem.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
@Tag(name = "Контроллер задач", description = "Контроллер, отвечающий за работу с задачами")
public class TaskController {
    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    @Transactional
    @Operation(
            summary = "Добавление задачи",
            description = "Позволяет автору добавить новую задачу"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<TaskServiceResponseModel> createTask(
            @RequestBody @Valid @Parameter(description = "Request Body запроса с информацией о новой задаче", required = true) GeneralTaskDBO dto,
            BindingResult bindingResult
            ) {
        return new ResponseEntity<>(taskService.createTask(dto, bindingResult), HttpStatus.CREATED);
    }

    @PutMapping("/me/{taskId}")
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    @Operation(
            summary = "Обновление задачи",
            description = "Позволяет автору обновить задачу"
    )
    @SecurityRequirement(name = "JWT")
    @Transactional
    public ResponseEntity<TaskServiceResponseModel> updateTaskByTaskId(
            @PathVariable(value = "taskId", required = false) @Parameter(description = "Id задачи для обновления", required = true) @NotNull @Min(1) Integer taskId,
            @RequestBody @Valid @Parameter(description = "Request Body запроса с информацией для обновления задачи", required = true)TaskDBOToUpdateTaskByTaskId dto,
            BindingResult bindingResult
    ) {
        return new ResponseEntity<>(taskService.updateTaskById(taskId, dto, bindingResult), HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    @Operation(
            summary = "Получение всех своих задач",
            description = "Позволяет пользователю получить все свои задачи"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<List<TaskServiceResponseModel>> getMyAllTasks() {
        return new ResponseEntity<>(taskService.getAllTasksFromAuthor(), HttpStatus.OK);
    }

    @GetMapping("/me/{taskId}")
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    @Operation(
            summary = "Получение своей задачи по id задачи",
            description = "Позволяет пользователю получить свою задачу по ее id"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<TaskServiceResponseModel> getMyTaskByTaskId(@PathVariable(value = "taskId", required = false) @Parameter(description = "Id задачи для поиска", required = true) @NotNull @Min(1) Integer taskId) {
        return new ResponseEntity<>(taskService.getTaskByTaskId(taskId), HttpStatus.OK);
    }

    @DeleteMapping("/me/{taskId}")
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    @Operation(
            summary = "Удаление своей задачи по id задачи",
            description = "Позволяет пользователю удалить свою задачу по ее id"
    )
    @SecurityRequirement(name = "JWT")
    @Transactional
    public void deleteMyTaskByTaskId(@PathVariable(value = "taskId", required = false) @Parameter(description = "Id задачи для удаления", required = true) @NotNull @Min(1) Integer taskId) {
        taskService.deleteTaskByTaskId(taskId);
    }

    @PutMapping("/author/{taskId}/status")
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    @Operation(
            summary = "Обновление статуса своей задачи по id задачи",
            description = "Позволяет пользователю обновить статус своей задачи по ее id"
    )
    @SecurityRequirement(name = "JWT")
    @Transactional
    public ResponseEntity<TaskServiceResponseModel> updateStatusOfTaskByTaskIdForAuthor(
            @PathVariable(value = "taskId", required = false) @Parameter(description = "Id задачи для обновления", required = true) @NotNull @Min(1) Integer taskId,
            @RequestBody @Valid @Parameter(description = "Request Body с новым статусом", required = true) StatusDBO dto,
            BindingResult bindingResult
            ) {
        return new ResponseEntity<>(taskService.updateStatusOfTaskByTaskIdForAuthor(taskId, dto, bindingResult), HttpStatus.OK);
    }

    @PutMapping("/{taskId}/executor/{executorId}")
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    @Operation(
            summary = "Обновление исполнителя своей задачи по id задачи",
            description = "Позволяет пользователю обновить исполнителя своей задачи по ее id"
    )
    @SecurityRequirement(name = "JWT")
    @Transactional
    public ResponseEntity<TaskServiceResponseModel> updateExecutorOfTaskByTaskId(
            @PathVariable(value = "taskId", required = false) @Parameter(description = "Id задачи для обновления", required = true) @NotNull @Min(1) Integer taskId,
            @PathVariable(value = "executorId", required = false) @Parameter(description = "Id исполнителя для обновления", required = true) @NotNull @Min(1) Integer executorId
    ) {
        return new ResponseEntity<>(taskService.updateExecutorOfTaskByTaskId(taskId, executorId), HttpStatus.OK);
    }

    @PutMapping("/executor/{taskId}/status")
    @PreAuthorize("hasRole('ROLE_EXECUTOR')")
    @Async
    @Operation(
            summary = "Обновление исполнителем статуса своей задачи по id задачи",
            description = "Позволяет исполнителю обновить статус своей задачи по ее id"
    )
    @SecurityRequirement(name = "JWT")
    @Transactional
    public ResponseEntity<TaskServiceResponseModel> updateStatusOfTaskByTaskIdForExecutor(
            @PathVariable(value = "taskId", required = false) @Parameter(description = "Id задачи для обновления", required = true) @NotNull @Min(1) Integer taskId,
            @RequestBody @Valid @Parameter(description = "Request Body с новым статусом", required = true) StatusDBO dto,
            BindingResult bindingResult
            ) {
        return new ResponseEntity<>(taskService.updateStatusOfTaskByTaskIdForExecutor(taskId, dto, bindingResult), HttpStatus.OK);
    }

    @GetMapping("/{accountId}/search")
    @Async
    @Operation(
            summary = "Получение авторизованным пользователем задач по accountId и фильтрам",
            description = "Позволяет авторизованному пользователю получить задачи по accountId и фильтрам"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<List<TaskServiceResponseModel>> getTasksByAccountIdAndFilters(
            @PathVariable(value = "accountId", required = false) @Parameter(description = "Id аккаунта для поиска", required = true) @NotNull @Min(1) Integer accountId,
            @RequestParam(value = "status", required = false, defaultValue = "") @Parameter(description = "Статус для поиска") String status,
            @RequestParam(value = "priority", required = false, defaultValue = "") @Parameter(description = "Приоритет для поиска") String priority
    ) {
        return new ResponseEntity<>(taskService.getTasksByAccountIdAndFilters(accountId, status, priority), HttpStatus.OK);
    }
}