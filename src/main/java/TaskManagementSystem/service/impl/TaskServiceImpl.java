package TaskManagementSystem.service.impl;

import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.DBOToCreateTask;
import TaskManagementSystem.presenter.TaskPresenter;
import TaskManagementSystem.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class TaskServiceImpl implements TaskService {
    private TaskPresenter taskPresenter;



    @Override
    public GeneralTaskDSResponseModel createTask(DBOToCreateTask dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw taskPresenter.prepareBadRequestView("Неверные входные данные");

        // доделать проверки
    }
}
