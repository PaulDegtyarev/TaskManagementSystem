package TaskManagementSystem.factory.impl;

import TaskManagementSystem.dto.serviceResponse.TaskServiceResponseModel;
import TaskManagementSystem.entity.TaskEntity;
import TaskManagementSystem.factory.ServiceResponseFactory;
import org.springframework.stereotype.Component;

@Component
public class ServiceResponseFactoryImpl implements ServiceResponseFactory {
    @Override
    public TaskServiceResponseModel createGeneralResponse(TaskEntity taskEntity) {
        return new TaskServiceResponseModel(
                taskEntity.getTaskId(),
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getStatusEntity().getStatus(),
                taskEntity.getPriorityEntity().getPriority(),
                taskEntity.getAuthorEntity().getEmail(),
                taskEntity.getExecutorEntity().getEmail(),
                taskEntity.getComment()
        );
    }
}
