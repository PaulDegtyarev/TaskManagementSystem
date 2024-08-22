package TaskManagementSystem.factory;

import TaskManagementSystem.dto.serviceResponse.TaskServiceResponseModel;
import TaskManagementSystem.entity.TaskEntity;

public interface ServiceResponseFactory {
    TaskServiceResponseModel createGeneralResponse(TaskEntity taskEntity);
}
