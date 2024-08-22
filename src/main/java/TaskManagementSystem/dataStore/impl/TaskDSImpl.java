package TaskManagementSystem.dataStore.impl;

import TaskManagementSystem.dataStore.TaskDS;
import TaskManagementSystem.dto.dSRequest.TaskDSRequestModel;
import TaskManagementSystem.dto.serviceResponse.TaskServiceResponseModel;
import TaskManagementSystem.dto.dbo.TaskDBOToUpdateTaskByTaskId;
import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.entity.PriorityEntity;
import TaskManagementSystem.entity.StatusEntity;
import TaskManagementSystem.entity.TaskEntity;
import TaskManagementSystem.factory.ServiceResponseFactory;
import TaskManagementSystem.repository.AccountRepository;
import TaskManagementSystem.repository.PriorityRepository;
import TaskManagementSystem.repository.StatusRepository;
import TaskManagementSystem.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskDSImpl implements TaskDS {
    private AccountRepository accountRepository;
    private StatusRepository statusRepository;
    private PriorityRepository priorityRepository;
    private TaskRepository taskRepository;
    private ServiceResponseFactory serviceResponseFactory;

    @Autowired
    public TaskDSImpl(AccountRepository accountRepository, StatusRepository statusRepository, PriorityRepository priorityRepository, TaskRepository taskRepository, ServiceResponseFactory serviceResponseFactory) {
        this.accountRepository = accountRepository;
        this.statusRepository = statusRepository;
        this.priorityRepository = priorityRepository;
        this.taskRepository = taskRepository;
        this.serviceResponseFactory = serviceResponseFactory;
    }

    @Override
    public TaskServiceResponseModel createTask(TaskDSRequestModel dsRequest) {
        AccountEntity authorEntity = accountRepository
                .findById(dsRequest.getAuthorId())
                .get();

        AccountEntity executorEntity = accountRepository
                .findById(dsRequest.getExecutorId())
                .get();

        StatusEntity statusEntity = statusRepository
                .findByStatus(dsRequest.getStatus().toLowerCase())
                .get();

        PriorityEntity priorityEntity = priorityRepository
                .findByPriority(dsRequest.getPriority().toLowerCase())
                .get();

        TaskEntity newTask = new TaskEntity(
                dsRequest.getTitle(),
                dsRequest.getDescription(),
                statusEntity.getStatusId(),
                priorityEntity.getPriorityId(),
                authorEntity.getAccountId(),
                executorEntity.getAccountId(),
                dsRequest.getComment()
        );

        newTask.setEntities(authorEntity, executorEntity, statusEntity, priorityEntity);

        taskRepository.save(newTask);

        return serviceResponseFactory.createGeneralResponse(newTask);
    }

    @Override
    public TaskServiceResponseModel updateTaskById(Integer taskId, TaskDBOToUpdateTaskByTaskId dto) {
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

        StatusEntity statusEntity = statusRepository
                .findByStatus(dto.getStatus().toLowerCase())
                        .get();

        foundTask.updateTaskEntity(dto, authorEntity, executorEntity, priorityEntity, statusEntity);
        taskRepository.save(foundTask);

        return serviceResponseFactory.createGeneralResponse(foundTask);
    }

    @Override
    public List<TaskServiceResponseModel> getAllTasksByAuthorId(Integer authorId) {
        return accountRepository
                .findById(authorId)
                .get()
                .getTaskEntitiesByAuthor()
                .stream()
                .map(taskEntity -> serviceResponseFactory.createGeneralResponse(taskEntity))
                .toList();
    }

    @Override
    public TaskServiceResponseModel getTaskByTaskId(Integer taskId) {
        return taskRepository
                .findById(taskId)
                .map(serviceResponseFactory::createGeneralResponse)
                .get();
    }
}
