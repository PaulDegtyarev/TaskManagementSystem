package TaskManagementSystem.factory;

import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.entity.TaskEntity;

public interface DSResponseFactory {
    GeneralTaskDSResponseModel createGeneralResponse(TaskEntity taskEntity);
}
