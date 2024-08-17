package TaskManagementSystem.service;

import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.TaskDBO;
import org.springframework.validation.BindingResult;

public interface TaskService {
    GeneralTaskDSResponseModel createTask(TaskDBO dto, BindingResult bindingResult);

    GeneralTaskDSResponseModel updateTaskById(Integer taskId, TaskDBO dto, BindingResult bindingResult);
}
