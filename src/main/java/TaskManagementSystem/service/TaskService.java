package TaskManagementSystem.service;

import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.DBOToCreateTask;
import org.springframework.validation.BindingResult;

public interface TaskService {
    GeneralTaskDSResponseModel createTask(DBOToCreateTask dto, BindingResult bindingResult);
}
