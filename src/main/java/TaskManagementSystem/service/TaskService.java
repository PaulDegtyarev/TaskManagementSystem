package TaskManagementSystem.service;

import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.GeneralTaskDBO;
import TaskManagementSystem.dto.dbo.StatusDBO;
import TaskManagementSystem.dto.dbo.TaskDBOToUpdateTaskByTaskId;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface TaskService {
    GeneralTaskDSResponseModel createTask(GeneralTaskDBO dto, BindingResult bindingResult);

    GeneralTaskDSResponseModel updateTaskById(Integer taskId, TaskDBOToUpdateTaskByTaskId dto, BindingResult bindingResult);

    List<GeneralTaskDSResponseModel> getAllTasksFromAuthor();

    GeneralTaskDSResponseModel getTaskByTaskId(Integer taskId);

    void deleteTaskByTaskId(Integer taskId);

    GeneralTaskDSResponseModel updateStatusOfTaskByTaskIdForAuthor(Integer taskId, StatusDBO dto, BindingResult bindingResult);

    GeneralTaskDSResponseModel updateExecutorOfTaskByTaskId(Integer taskId, Integer executorId);

    GeneralTaskDSResponseModel updateStatusOfTaskByTaskIdForExecutor(Integer taskId, StatusDBO dto, BindingResult bindingResult);
}
