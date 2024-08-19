package TaskManagementSystem.dataStore;

import TaskManagementSystem.dto.dSRequest.TaskDSRequestModel;
import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.GeneralTaskDBO;
import TaskManagementSystem.dto.dbo.TaskDBOToUpdateTaskByTaskId;

import java.util.List;

public interface TaskDS {


    GeneralTaskDSResponseModel createTask(TaskDSRequestModel dsRequest);


    GeneralTaskDSResponseModel updateTaskById(Integer taskId, TaskDBOToUpdateTaskByTaskId dto);

    List<GeneralTaskDSResponseModel> getAllTasksByAuthorId(Integer authorId);

    GeneralTaskDSResponseModel getTaskByTaskId(Integer taskId);
}
