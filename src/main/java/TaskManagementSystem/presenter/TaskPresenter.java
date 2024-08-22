package TaskManagementSystem.presenter;

import TaskManagementSystem.dto.serviceResponse.TaskServiceResponseModel;
import TaskManagementSystem.exception.task.TaskBadRequestException;
import TaskManagementSystem.exception.task.TaskForbiddenException;
import TaskManagementSystem.exception.task.TaskNotFoundException;

import java.util.List;

public interface TaskPresenter {
    TaskBadRequestException prepareBadRequestView(String message);

    TaskNotFoundException prepareNotFoundView(String message);

    TaskForbiddenException prepareForbiddenView(String message);

    TaskServiceResponseModel prepareSuccessView(TaskServiceResponseModel response);

    List<TaskServiceResponseModel> prepareSuccessView(List<TaskServiceResponseModel> response);
}
