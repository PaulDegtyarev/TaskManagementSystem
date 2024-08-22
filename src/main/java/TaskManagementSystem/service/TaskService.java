package TaskManagementSystem.service;

import TaskManagementSystem.dto.serviceResponse.TaskServiceResponseModel;
import TaskManagementSystem.dto.dbo.GeneralTaskDBO;
import TaskManagementSystem.dto.dbo.StatusDBO;
import TaskManagementSystem.dto.dbo.TaskDBOToUpdateTaskByTaskId;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface TaskService {
    TaskServiceResponseModel createTask(GeneralTaskDBO dto, BindingResult bindingResult);

    TaskServiceResponseModel updateTaskById(Integer taskId, TaskDBOToUpdateTaskByTaskId dto, BindingResult bindingResult);

    List<TaskServiceResponseModel> getAllTasksFromAuthor();

    TaskServiceResponseModel getTaskByTaskId(Integer taskId);

    void deleteTaskByTaskId(Integer taskId);

    TaskServiceResponseModel updateStatusOfTaskByTaskIdForAuthor(Integer taskId, StatusDBO dto, BindingResult bindingResult);

    TaskServiceResponseModel updateExecutorOfTaskByTaskId(Integer taskId, Integer executorId);

    TaskServiceResponseModel updateStatusOfTaskByTaskIdForExecutor(Integer taskId, StatusDBO dto, BindingResult bindingResult);

//    List<GeneralTaskDSResponseModel> getTasksByAccountIdAndFilters(Integer accountId, Optional<String> status, Optional<String> priority);
    List<TaskServiceResponseModel> getTasksByAccountIdAndFilters(Integer accountId, String status, String priority);
}
