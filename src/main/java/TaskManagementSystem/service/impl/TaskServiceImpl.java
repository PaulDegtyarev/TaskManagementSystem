package TaskManagementSystem.service.impl;

import TaskManagementSystem.config.MyUserDetails;
import TaskManagementSystem.dataStore.TaskDS;
import TaskManagementSystem.dto.dSRequest.TaskDSRequestModel;
import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.GeneralTaskDBO;
import TaskManagementSystem.dto.dbo.TaskDBOToUpdateTaskByTaskId;
import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.entity.TaskEntity;
import TaskManagementSystem.exception.task.TaskBadRequestException;
import TaskManagementSystem.exception.task.TaskForbiddenException;
import TaskManagementSystem.exception.task.TaskNotFoundException;
import TaskManagementSystem.presenter.TaskPresenter;
import TaskManagementSystem.repository.AccountRepository;
import TaskManagementSystem.repository.PriorityRepository;
import TaskManagementSystem.repository.StatusRepository;
import TaskManagementSystem.repository.TaskRepository;
import TaskManagementSystem.service.TaskService;
import TaskManagementSystem.util.RoleUtil;
import TaskManagementSystem.util.StatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {
    private TaskPresenter taskPresenter;
    private AccountRepository accountRepository;
    private StatusRepository statusRepository;
    private PriorityRepository priorityRepository;
    private TaskDS taskDS;
    private TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskPresenter taskPresenter, AccountRepository accountRepository, StatusRepository statusRepository, PriorityRepository priorityRepository, TaskDS taskDS, TaskRepository taskRepository) {
        this.taskPresenter = taskPresenter;
        this.accountRepository = accountRepository;
        this.statusRepository = statusRepository;
        this.priorityRepository = priorityRepository;
        this.taskDS = taskDS;
        this.taskRepository = taskRepository;
    }

    @Override
    public GeneralTaskDSResponseModel createTask(GeneralTaskDBO dto, BindingResult bindingResult) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Integer accountId = myUserDetails.getId();

        if (bindingResult.hasErrors()) throw taskPresenter.prepareBadRequestView("Неверные входные данные");

        Optional<AccountEntity> authorEntity = accountRepository.findById(dto.getAuthorId());

        if (authorEntity.isEmpty()) throw taskPresenter.prepareNotFoundView("Автор не найден");

        if (!authorEntity
                .get()
                .getAccountId()
                .equals(accountId)) throw taskPresenter.prepareBadRequestView("id автора не совпадает с Вашим");

        if (!authorEntity
                .get()
                .getRoleEntity()
                .getRole()
                .equals(RoleUtil.AUTHOR_En)) throw taskPresenter.prepareForbiddenView("Исполнитель не может быть автором");

        Optional<AccountEntity> executorEntity = accountRepository
                .findById(dto.getExecutorId());

        if (executorEntity.isEmpty()) throw taskPresenter.prepareNotFoundView("Исполнитель не найден");

        if (!executorEntity
                .get()
                .getRoleEntity()
                .getRole()
                .equals(RoleUtil.EXECUTOR_En)) throw taskPresenter.prepareForbiddenView("Автор не может быть исполнителем");

        if (priorityRepository
                .findByPriority(dto.getPriority().toLowerCase())
                .isEmpty()) throw taskPresenter.prepareNotFoundView("Приоритет не найден");

        TaskDSRequestModel dsRequest = new TaskDSRequestModel(
                dto.getTitle(),
                dto.getDescription(),
                dto.getPriority(),
                dto.getAuthorId(),
                dto.getExecutorId(),
                dto.getComment(),
                StatusUtil.WAITING
        );

        GeneralTaskDSResponseModel createdTask = taskDS.createTask(dsRequest);

        return taskPresenter.prepareSuccessView(createdTask);
    }

    @Override
    public GeneralTaskDSResponseModel updateTaskById(Integer taskId, TaskDBOToUpdateTaskByTaskId dto, BindingResult bindingResult) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Integer accountId = myUserDetails.getId();

        Optional<TaskEntity> taskEntity = taskRepository.findById(taskId);

        if (taskEntity.isEmpty()) throw taskPresenter.prepareNotFoundView("Задача не найдена");

        if (bindingResult.hasErrors()) throw taskPresenter.prepareBadRequestView("Неверные входные данные");

        Optional<AccountEntity> authorEntity = accountRepository.findById(dto.getAuthorId());

        if (authorEntity.isEmpty()) throw taskPresenter.prepareNotFoundView("Автор не найден");

        if (!authorEntity
                    .get()
                    .getRoleEntity()
                    .getRole()
                    .equals(RoleUtil.AUTHOR_En))
                throw taskPresenter.prepareForbiddenView("Исполнитель не может быть автором");

        if (!taskEntity
                .get()
                .getAuthorEntity()
                .getAccountId()
                .equals(accountId)) throw taskPresenter.prepareForbiddenView("Вы не можете обновить чужую задачу");

        Optional<AccountEntity> executorEntity = accountRepository.findById(dto.getExecutorId());

        if (executorEntity.isEmpty()) throw taskPresenter.prepareNotFoundView("Исполнитель не найден");

        if (!executorEntity
                    .get()
                    .getRoleEntity()
                    .getRole()
                    .equals(RoleUtil.EXECUTOR_En))
                throw taskPresenter.prepareForbiddenView("Автор не может быть исполнителем");

        if (priorityRepository
                    .findByPriority(dto.getPriority().toLowerCase())
                    .isEmpty()) throw taskPresenter.prepareNotFoundView("Приоритет не найден");

        if (statusRepository
                .findByStatus(dto.getStatus().toLowerCase())
                .isEmpty()) throw taskPresenter.prepareNotFoundView("Статус не найден");

        GeneralTaskDSResponseModel updatedTask = taskDS.updateTaskById(taskId, dto);

        return taskPresenter.prepareSuccessView(updatedTask);
    }

    @Override
    public List<GeneralTaskDSResponseModel> getAllTasksFromAuthor() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Integer authorId = myUserDetails.getId();

        List<GeneralTaskDSResponseModel> foundTasks = taskDS.getAllTasksByAuthorId(authorId);

        return taskPresenter.prepareSuccessView(foundTasks);
    }
    
    @Override
    public GeneralTaskDSResponseModel getTaskByTaskId(Integer taskId) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Integer accountId = myUserDetails.getId();

        Optional<TaskEntity> taskEntity = taskRepository.findById(taskId);

        if (taskEntity.isEmpty()) throw taskPresenter.prepareNotFoundView("Задача не найдена");

        if (!taskEntity
                .get()
                .getAuthorId()
                .equals(accountId)) throw taskPresenter.prepareBadRequestView("У Вас нет такой задачи");

        GeneralTaskDSResponseModel foundTask = taskDS.getTaskByTaskId(taskId);

        return taskPresenter.prepareSuccessView(foundTask);
    }

    @Override
    public void deleteTaskByTaskId(Integer taskId) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Integer accountId = myUserDetails.getId();

        TaskEntity taskEntity;

        try {
            taskEntity = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Задача не найдена"));

            if (!taskEntity
                    .getAuthorId()
                    .equals(accountId)) throw new TaskForbiddenException("Вы не можете удалить чужую задачу");
        } catch (TaskForbiddenException taskForbiddenException) {
            throw taskPresenter.prepareForbiddenView(taskForbiddenException.getMessage());
        } catch (TaskNotFoundException taskNotFoundException) {
            throw taskPresenter.prepareNotFoundView(taskNotFoundException.getMessage());
        }

        taskRepository.delete(taskEntity);
    }
}
