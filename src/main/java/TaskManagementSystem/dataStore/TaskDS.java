package TaskManagementSystem.dataStore;

import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.TaskDBO;

public interface TaskDS {
    GeneralTaskDSResponseModel createTask(TaskDBO dto);

    GeneralTaskDSResponseModel updateTaskById(Integer taskId, TaskDBO dto);
}
