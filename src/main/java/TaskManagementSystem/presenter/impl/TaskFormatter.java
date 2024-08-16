package TaskManagementSystem.presenter.impl;

import TaskManagementSystem.exception.task.TaskBadRequestException;
import TaskManagementSystem.presenter.TaskPresenter;

public class TaskFormatter implements TaskPresenter {
    @Override
    public TaskBadRequestException prepareBadRequestView(String message) {throw new TaskBadRequestException(message);}
}
