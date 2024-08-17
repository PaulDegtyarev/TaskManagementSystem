package TaskManagementSystem.presenter;

import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.exception.task.TaskBadRequestException;
import TaskManagementSystem.exception.task.TaskForbiddenException;
import TaskManagementSystem.exception.task.TaskNotFoundException;

public interface TaskPresenter {
    TaskBadRequestException prepareBadRequestView(String message);

    TaskNotFoundException prepareNotFoundView(String message);

    TaskForbiddenException prepareForbiddenView(String message);

    GeneralTaskDSResponseModel prepareSuccessView(GeneralTaskDSResponseModel response);
}
