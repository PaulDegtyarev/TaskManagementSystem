package TaskManagementSystem.controller;

import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.DBOToCreateTask;
import TaskManagementSystem.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
public class TaskController {
    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("")
    @Async
    @Transactional
    public ResponseEntity<GeneralTaskDSResponseModel> createTask(
            @RequestBody @Valid DBOToCreateTask dto,
            BindingResult bindingResult
            ) {
        return new ResponseEntity<>(taskService.createTask(dto, bindingResult), HttpStatus.CREATED);
    }
}
