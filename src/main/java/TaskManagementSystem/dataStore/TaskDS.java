package TaskManagementSystem.dataStore;

import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.TaskDBO;

import java.util.List;

public interface TaskDS {
    GeneralTaskDSResponseModel createTask(TaskDBO dto);

    GeneralTaskDSResponseModel updateTaskById(Integer taskId, TaskDBO dto);

    List<GeneralTaskDSResponseModel> getAllTasksByAuthorId(Integer authorId);

    GeneralTaskDSResponseModel getTaskByTaskId(Integer taskId);
}
