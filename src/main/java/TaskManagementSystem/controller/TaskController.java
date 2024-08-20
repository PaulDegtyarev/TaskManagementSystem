package TaskManagementSystem.controller;

import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.GeneralTaskDBO;
import TaskManagementSystem.dto.dbo.StatusDBO;
import TaskManagementSystem.dto.dbo.TaskDBOToUpdateTaskByTaskId;
import TaskManagementSystem.service.TaskService;
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
    public ResponseEntity<GeneralTaskDSResponseModel> createTask(
            @RequestBody @Valid GeneralTaskDBO dto,
            BindingResult bindingResult
            ) {
        return new ResponseEntity<>(taskService.createTask(dto, bindingResult), HttpStatus.CREATED);
    }

    @PutMapping(value = {"/", "/{taskId}"})
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    public ResponseEntity<GeneralTaskDSResponseModel> updateTaskByTaskId(
            @PathVariable(value = "taskId", required = false) @NotNull @Min(1) Integer taskId,
            @RequestBody @Valid TaskDBOToUpdateTaskByTaskId dto,
            BindingResult bindingResult
    ) {
        return new ResponseEntity<>(taskService.updateTaskById(taskId, dto, bindingResult), HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    public ResponseEntity<List<GeneralTaskDSResponseModel>> getMyAllTasks() {
        return new ResponseEntity<>(taskService.getAllTasksFromAuthor(), HttpStatus.OK);
    }

    @GetMapping(value = {"/me/", "/me/{taskId}"})
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    public ResponseEntity<GeneralTaskDSResponseModel> getMyTaskByTaskId(@PathVariable(value = "taskId", required = false) @NotNull @Min(1) Integer taskId) {
        return new ResponseEntity<>(taskService.getTaskByTaskId(taskId), HttpStatus.OK);
    }

    @DeleteMapping(value = {"/me/", "/me/{taskId}"})
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    public void deleteMyTaskByTaskId(@PathVariable(value = "taskId", required = false) @NotNull @Min(1) Integer taskId) {
        taskService.deleteTaskByTaskId(taskId);
    }

    @PutMapping(value = {"/status" ,"/{taskId}/status"})
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    public ResponseEntity<GeneralTaskDSResponseModel> updateStatusOfTaskByTaskIdForAuthor(
            @PathVariable(value = "taskId", required = false) @NotNull @Min(1) Integer taskId,
            @RequestBody @Valid StatusDBO dto,
            BindingResult bindingResult
            ) {
        return new ResponseEntity<>(taskService.updateStatusOfTaskByTaskIdForAuthor(taskId, dto, bindingResult), HttpStatus.OK);
    }

    @PutMapping(value = {"/{taskId}/executor/{executorId}", "/executor/{executorId}", "/{taskId}/executor/", "/executor/"})
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    public ResponseEntity<GeneralTaskDSResponseModel> updateExecutorOfTaskByTaskId(
            @PathVariable(value = "taskId", required = false) @NotNull @Min(1) Integer taskId,
            @PathVariable(value = "executorId", required = false) @NotNull @Min(1) Integer executorId
    ) {
        return new ResponseEntity<>(taskService.updateExecutorOfTaskByTaskId(taskId, executorId), HttpStatus.OK);
    }

    @PutMapping(value = {"/executor/{taskId}", "/executor/"})
    @PreAuthorize("hasRole('ROLE_EXECUTOR')")
    @Async
    public ResponseEntity<GeneralTaskDSResponseModel> updateStatusOfTaskByTaskIdForExecutor(
            @PathVariable(value = "taskId", required = false) @NotNull @Min(1) Integer taskId,
            @RequestBody @Valid StatusDBO dto
            ) {
        return new ResponseEntity<>(taskService.updateStatusOfTaskByTaskIdForExecutor(taskId, dto), HttpStatus.OK);
    }
}
