package TaskManagementSystem.dataStore;

import TaskManagementSystem.dto.dSRequest.TaskDSRequestModel;
import TaskManagementSystem.dto.serviceResponse.TaskServiceResponseModel;
import TaskManagementSystem.dto.dbo.TaskDBOToUpdateTaskByTaskId;

import java.util.List;

public interface TaskDS {


    TaskServiceResponseModel createTask(TaskDSRequestModel dsRequest);


    TaskServiceResponseModel updateTaskById(Integer taskId, TaskDBOToUpdateTaskByTaskId dto);

    List<TaskServiceResponseModel> getAllTasksByAuthorId(Integer authorId);

    TaskServiceResponseModel getTaskByTaskId(Integer taskId);
}
