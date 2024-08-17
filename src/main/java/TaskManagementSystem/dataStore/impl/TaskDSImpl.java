package TaskManagementSystem.dataStore.impl;

import TaskManagementSystem.dataStore.TaskDS;
import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.TaskDBO;
import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.entity.PriorityEntity;
import TaskManagementSystem.entity.StatusEntity;
import TaskManagementSystem.entity.TaskEntity;
import TaskManagementSystem.factory.DSResponseFactory;
import TaskManagementSystem.repository.AccountRepository;
import TaskManagementSystem.repository.PriorityRepository;
import TaskManagementSystem.repository.StatusRepository;
import TaskManagementSystem.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskDSImpl implements TaskDS {
    private AccountRepository accountRepository;
    private StatusRepository statusRepository;
    private PriorityRepository priorityRepository;
    private TaskRepository taskRepository;
    private DSResponseFactory dsResponseFactory;

    @Autowired
    public TaskDSImpl(AccountRepository accountRepository, StatusRepository statusRepository, PriorityRepository priorityRepository, TaskRepository taskRepository, DSResponseFactory dsResponseFactory) {
        this.accountRepository = accountRepository;
        this.statusRepository = statusRepository;
        this.priorityRepository = priorityRepository;
        this.taskRepository = taskRepository;
        this.dsResponseFactory = dsResponseFactory;
    }

    @Override
    public GeneralTaskDSResponseModel createTask(TaskDBO dto) {
        AccountEntity authorEntity = accountRepository
                .findById(dto.getAuthorId())
                .get();

        AccountEntity executorEntity = accountRepository
                .findById(dto.getExecutorId())
                .get();

        StatusEntity statusEntity = statusRepository
                .findByStatus(dto.getStatus().toLowerCase())
                .get();

        PriorityEntity priorityEntity = priorityRepository
                .findByPriority(dto.getPriority().toLowerCase())
                .get();

        TaskEntity newTask = new TaskEntity(
                dto.getTitle(),
                dto.getDescription(),
                statusEntity.getStatusId(),
                priorityEntity.getPriorityId(),
                authorEntity.getAccountId(),
                executorEntity.getAccountId(),
                dto.getComment()
        );

        newTask.setEntities(authorEntity, executorEntity, statusEntity, priorityEntity);

        taskRepository.save(newTask);

        return dsResponseFactory.createGeneralResponse(newTask);
    }

    @Override
    public GeneralTaskDSResponseModel updateTaskById(Integer taskId, TaskDBO dto) {
        TaskEntity foundTask = taskRepository.findById(taskId).get();

        AccountEntity authorEntity = accountRepository
                .findById(dto.getAuthorId())
                .get();

        AccountEntity executorEntity = accountRepository
                .findById(dto.getExecutorId())
                .get();

        PriorityEntity priorityEntity = priorityRepository
                .findByPriority(dto.getPriority().toLowerCase())
                .get();

        foundTask.updateTaskEntity(dto, authorEntity, executorEntity, priorityEntity);
        taskRepository.save(foundTask);

        return dsResponseFactory.createGeneralResponse(foundTask);
    }
}
