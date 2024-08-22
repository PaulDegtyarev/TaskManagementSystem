package TaskManagementSystem.presenter.impl;

import TaskManagementSystem.dto.serviceResponse.TaskServiceResponseModel;
import TaskManagementSystem.exception.task.TaskBadRequestException;
import TaskManagementSystem.exception.task.TaskForbiddenException;
import TaskManagementSystem.exception.task.TaskNotFoundException;
import TaskManagementSystem.presenter.TaskPresenter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskFormatter implements TaskPresenter {
    @Override
    public TaskBadRequestException prepareBadRequestView(String message) {throw new TaskBadRequestException(message);}

    @Override
    public TaskNotFoundException prepareNotFoundView(String message) {throw new TaskNotFoundException(message);}

    @Override
    public TaskForbiddenException prepareForbiddenView(String message) {throw new TaskForbiddenException(message);}

    @Override
    public TaskServiceResponseModel prepareSuccessView(TaskServiceResponseModel response) {return response;}

    @Override
    public List<TaskServiceResponseModel> prepareSuccessView(List<TaskServiceResponseModel> response) {return response;}
}