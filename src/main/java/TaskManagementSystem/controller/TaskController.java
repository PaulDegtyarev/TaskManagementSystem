package TaskManagementSystem.controller;

import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.TaskDBO;
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
            @RequestBody @Valid TaskDBO dto,
            BindingResult bindingResult
            ) {
        return new ResponseEntity<>(taskService.createTask(dto, bindingResult), HttpStatus.CREATED);
    }

    @PutMapping(value = {"", "/{taskId}"})
    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @Async
    public ResponseEntity<GeneralTaskDSResponseModel> updateTaskByTaskId(
            @PathVariable @NotNull @Min(1) Integer taskId,
            @RequestBody @Valid TaskDBO dto,
            BindingResult bindingResult
    ) {
        return new ResponseEntity<>(taskService.updateTaskById(taskId, dto, bindingResult), HttpStatus.OK);
    }
}
