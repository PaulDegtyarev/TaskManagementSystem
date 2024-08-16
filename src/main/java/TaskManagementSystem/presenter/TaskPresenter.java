package TaskManagementSystem.presenter;

import TaskManagementSystem.exception.task.TaskBadRequestException;

public interface TaskPresenter {
    TaskBadRequestException prepareBadRequestView(String message);
}
