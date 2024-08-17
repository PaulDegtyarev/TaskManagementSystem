package TaskManagementSystem.factory.impl;

import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.entity.TaskEntity;
import TaskManagementSystem.factory.DSResponseFactory;
import org.springframework.stereotype.Component;

@Component
public class DSResponseFactoryImpl implements DSResponseFactory {
    @Override
    public GeneralTaskDSResponseModel createGeneralResponse(TaskEntity taskEntity) {
        return new GeneralTaskDSResponseModel(
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
