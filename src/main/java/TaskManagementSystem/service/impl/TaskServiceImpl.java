package TaskManagementSystem.service.impl;

import TaskManagementSystem.config.MyUserDetails;
import TaskManagementSystem.dataStore.TaskDS;
import TaskManagementSystem.dto.dSRequest.TaskDSRequestModel;
import TaskManagementSystem.dto.serviceResponse.TaskServiceResponseModel;
import TaskManagementSystem.dto.dbo.GeneralTaskDBO;
import TaskManagementSystem.dto.dbo.StatusDBO;
import TaskManagementSystem.dto.dbo.TaskDBOToUpdateTaskByTaskId;
import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.entity.StatusEntity;
import TaskManagementSystem.entity.TaskEntity;
import TaskManagementSystem.exception.task.TaskBadRequestException;
import TaskManagementSystem.exception.task.TaskForbiddenException;
import TaskManagementSystem.exception.task.TaskNotFoundException;
import TaskManagementSystem.factory.ServiceResponseFactory;
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
    private ServiceResponseFactory serviceResponseFactory;

    @Autowired
    public TaskServiceImpl(TaskPresenter taskPresenter, AccountRepository accountRepository, StatusRepository statusRepository, PriorityRepository priorityRepository, TaskDS taskDS, TaskRepository taskRepository, ServiceResponseFactory serviceResponseFactory) {
        this.taskPresenter = taskPresenter;
        this.accountRepository = accountRepository;
        this.statusRepository = statusRepository;
        this.priorityRepository = priorityRepository;
        this.taskDS = taskDS;
        this.taskRepository = taskRepository;
        this.serviceResponseFactory = serviceResponseFactory;
    }

    /**
     * Создание новой задачи.
     *
     * @param dto объект {@link GeneralTaskDBO}, содержащий данные о задаче
     * @param bindingResult объект {@link BindingResult}, содержащий любые ошибки валидации
     * @return объект {@link TaskServiceResponseModel}, представляющий созданную задачу
     * @throws TaskNotFoundException   если автор или исполнитель не найдены
     * @throws TaskBadRequestException если идентификатор автора не совпадает с идентификатором аутентифицированного пользователя или если автор является исполнителем
     * @throws TaskForbiddenException  если аутентифицированный пользователь не авторизован на создание задачи
     */
    @Override
    public TaskServiceResponseModel createTask(GeneralTaskDBO dto, BindingResult bindingResult) {
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

        TaskServiceResponseModel createdTask = taskDS.createTask(dsRequest);

        return taskPresenter.prepareSuccessView(createdTask);
    }

    /**
     * Обновляет существующую задачу по указанному идентификатору задачи.
     *
     * @param taskId идентификатор задачи, которую нужно обновить
     * @param dto объект {@link TaskDBOToUpdateTaskByTaskId}, содержащий обновленные данные задачи
     * @param bindingResult объект {@link BindingResult}, содержащий любые ошибки валидации
     * @return объект {@link TaskServiceResponseModel}, представляющий обновленную задачу
     * @throws TaskNotFoundException если задача, автор или исполнитель не найдены
     * @throws TaskBadRequestException если входные данные недействительны
     * @throws TaskForbiddenException если аутентифицированный пользователь не авторизован на обновление задачи
     */
    @Override
    public TaskServiceResponseModel updateTaskById(Integer taskId, TaskDBOToUpdateTaskByTaskId dto, BindingResult bindingResult) {
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

        TaskServiceResponseModel updatedTask = taskDS.updateTaskById(taskId, dto);

        return taskPresenter.prepareSuccessView(updatedTask);
    }

    /**
     * Возвращает список всех задач, созданных текущим аутентифицированным пользователем.
     *
     * @return список объектов {@link TaskServiceResponseModel}, представляющих найденные задачи
     */
    @Override
    public List<TaskServiceResponseModel> getAllTasksFromAuthor() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Integer authorId = myUserDetails.getId();

        List<TaskServiceResponseModel> foundTasks = taskDS.getAllTasksByAuthorId(authorId);

        return taskPresenter.prepareSuccessView(foundTasks);
    }

    /**
     * Возвращает задачу по указанному идентификатору задачи, если она принадлежит текущему аутентифицированному пользователю.
     *
     * @param taskId идентификатор задачи, которую нужно найти
     * @return объект {@link TaskServiceResponseModel}, представляющий найденную задачу
     * @throws TaskNotFoundException если задача не найдена
     * @throws TaskBadRequestException если у аутентифицированного пользователя нет задачи с указанным идентификатором
     */
    @Override
    public TaskServiceResponseModel getTaskByTaskId(Integer taskId) {
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

        TaskServiceResponseModel foundTask = taskDS.getTaskByTaskId(taskId);

        return taskPresenter.prepareSuccessView(foundTask);
    }

    /**
     * Удаляет существующую задачу по указанному идентификатору задачи, если она принадлежит текущему аутентифицированному пользователю.
     *
     * @param taskId идентификатор задачи, которую нужно удалить
     * @throws TaskNotFoundException если задача не найдена
     * @throws TaskForbiddenException если аутентифицированный пользователь не авторизован на удаление задачи
     */
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

    /**
     * Обновляет статус существующей задачи по указанному идентификатору задачи, если она принадлежит текущему аутентифицированному пользователю.
     *
     * @param taskId идентификатор задачи, статус которой нужно обновить
     * @param dto объект {@link StatusDBO} с новым статусом задачи
     * @param bindingResult объект {@link BindingResult} для обработки ошибок валидации входных данных
     * @return объект {@link TaskServiceResponseModel}, представляющий задачу с обновленным статусом
     * @throws TaskNotFoundException если задача не найдена
     * @throws TaskForbiddenException если аутентифицированный пользователь не авторизован на обновление статуса задачи
     * @throws TaskBadRequestException если входные данные в {@link StatusDBO} некорректны
     */
    @Override
    public TaskServiceResponseModel updateStatusOfTaskByTaskIdForAuthor(Integer taskId, StatusDBO dto, BindingResult bindingResult) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Integer accountId = myUserDetails.getId();

        TaskEntity taskEntity;
        StatusEntity statusEntity;

        try {
            taskEntity = taskRepository
                    .findById(taskId)
                    .orElseThrow(() -> new TaskNotFoundException("Задача не найдена"));

            if (!taskEntity
                    .getAuthorId()
                    .equals(accountId)) throw new TaskForbiddenException("Вы не можете обновить статус чужой задачи");

            if (bindingResult.hasErrors()) throw new TaskBadRequestException("Неверно введенные данные");

            statusEntity = statusRepository
                    .findByStatus(dto
                            .getStatus()
                            .toLowerCase())
                    .orElseThrow(() -> new TaskNotFoundException("Статус не найден"));

        } catch (TaskNotFoundException taskNotFoundException) {
            throw taskPresenter.prepareNotFoundView(taskNotFoundException.getMessage());
        } catch (TaskForbiddenException taskForbiddenException) {
            throw taskPresenter.prepareForbiddenView(taskForbiddenException.getMessage());
        } catch (TaskBadRequestException taskBadRequestException) {
            throw taskPresenter.prepareBadRequestView(taskBadRequestException.getMessage());
        }

        taskEntity.updateStatus(statusEntity);
        taskRepository.save(taskEntity);

        TaskServiceResponseModel taskWithUpdatedStatus = serviceResponseFactory.createGeneralResponse(taskEntity);

        return taskPresenter.prepareSuccessView(taskWithUpdatedStatus);
    }

    /**
     * Обновляет исполнителя существующей задачи по указанному идентификатору задачи, если текущий аутентифицированный пользователь является автором этой задачи.
     *
     * @param taskId идентификатор задачи, исполнителя которой нужно обновить
     * @param executorId идентификатор аккаунта нового исполнителя задачи
     * @return объект {@link TaskServiceResponseModel}, представляющий задачу с обновленным исполнителем
     * @throws TaskNotFoundException если задача или новый исполнитель не найдены
     * @throws TaskForbiddenException если аутентифицированный пользователь не является автором задачи
     * @throws TaskBadRequestException если указанный аккаунт не имеет роли исполнителя
     */
    @Override
    public TaskServiceResponseModel updateExecutorOfTaskByTaskId(Integer taskId, Integer executorId) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Integer accountId = myUserDetails.getId();

        TaskEntity taskEntityToUpdateExecutor;
        AccountEntity executorEntity;

        try {
            taskEntityToUpdateExecutor = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Задача не найдена"));

            if (!taskEntityToUpdateExecutor
                    .getAuthorId()
                    .equals(accountId)) {
                throw new TaskForbiddenException("Вы не можете обновить исполнителя чужой задачи");
            }

            executorEntity = accountRepository.findById(executorId).orElseThrow(() -> new TaskNotFoundException("Исполнитель не найден"));

            if (!executorEntity
                    .getRoleEntity()
                    .getRole()
                    .equals(RoleUtil.EXECUTOR_En)) throw new TaskBadRequestException("Автор не может быть исполнителем");

        } catch (TaskNotFoundException taskNotFoundException) {
            throw taskPresenter.prepareNotFoundView(taskNotFoundException.getMessage());
        } catch (TaskForbiddenException taskForbiddenException) {
            throw taskPresenter.prepareForbiddenView(taskForbiddenException.getMessage());
        } catch (TaskBadRequestException taskBadRequestException) {
            throw taskPresenter.prepareBadRequestView(taskBadRequestException.getMessage());
        }

        taskEntityToUpdateExecutor.updateExecutor(executorEntity);
        taskRepository.save(taskEntityToUpdateExecutor);

        TaskServiceResponseModel updatedTask = serviceResponseFactory.createGeneralResponse(taskEntityToUpdateExecutor);

        return taskPresenter.prepareSuccessView(updatedTask);
    }

    /**
     * Обновляет статус существующей задачи по указанному идентификатору задачи, если текущий аутентифицированный пользователь является исполнителем этой задачи.
     *
     * @param taskId идентификатор задачи, статус которой нужно обновить
     * @param dto объект {@link StatusDBO} с новым статусом задачи
     * @param bindingResult объект {@link BindingResult} для обработки ошибок валидации входных данных
     * @return объект {@link TaskServiceResponseModel}, представляющий задачу с обновленным статусом
     * @throws TaskNotFoundException если задача или новый статус не найдены
     * @throws TaskForbiddenException если аутентифицированный пользователь не является исполнителем задачи
     * @throws TaskBadRequestException если входные данные в {@link StatusDBO} некорректны
     */
    @Override
    public TaskServiceResponseModel updateStatusOfTaskByTaskIdForExecutor(Integer taskId, StatusDBO dto, BindingResult bindingResult) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        Integer accountId = myUserDetails.getId();

        TaskEntity taskEntityToUpdateStatus;
        StatusEntity statusEntity;

        try {
            taskEntityToUpdateStatus = taskRepository
                    .findById(taskId)
                    .orElseThrow(() -> new TaskNotFoundException("Задача не найена"));

            if (!taskEntityToUpdateStatus
                    .getExecutorEntity()
                    .getAccountId()
                    .equals(accountId)) {
                throw new TaskForbiddenException("Вы не можете обновить статус чужой задачи");
            }

            if (bindingResult.hasErrors()) {throw new TaskBadRequestException("Данные неверно введены");}

            statusEntity = statusRepository
                    .findByStatus(dto
                            .getStatus()
                            .toLowerCase())
                    .orElseThrow(() -> new TaskNotFoundException("Статус не найден"));

        } catch (TaskNotFoundException taskNotFoundException) {
            throw taskPresenter.prepareNotFoundView(taskNotFoundException.getMessage());
        } catch (TaskBadRequestException taskBadRequestException) {
            throw taskPresenter.prepareBadRequestView(taskBadRequestException.getMessage());
        } catch (TaskForbiddenException taskForbiddenException) {
            throw taskPresenter.prepareForbiddenView(taskForbiddenException.getMessage());
        }

        taskEntityToUpdateStatus.updateStatus(statusEntity);
        taskRepository.save(taskEntityToUpdateStatus);

        TaskServiceResponseModel updatedTask = serviceResponseFactory.createGeneralResponse(taskEntityToUpdateStatus);

        return taskPresenter.prepareSuccessView(updatedTask);
    }

    /**
     * Получает список задач, связанных с указанным идентификатором аккаунта, и отфильтрованных по статусу и приоритету.
     *
     * @param accountId идентификатор аккаунта, для которого необходимо получить список задач
     * @param status фильтр по статусу задачи (необязательный параметр)
     * @param priority фильтр по приоритету задачи (необязательный параметр)
     * @return список объектов {@link TaskServiceResponseModel}, представляющих задачи, соответствующие указанным фильтрам
     * @throws TaskNotFoundException если аккаунт с указанным идентификатором не найден
     */
    @Override
    public List<TaskServiceResponseModel> getTasksByAccountIdAndFilters(Integer accountId, String status, String priority) {
        AccountEntity accountEntity;

        try {
            accountEntity = accountRepository
                    .findById(accountId)
                    .orElseThrow(() -> new TaskNotFoundException("Аккаунт не найден"));

        } catch (TaskNotFoundException taskNotFoundException) {
            throw taskPresenter.prepareNotFoundView(taskNotFoundException.getMessage());
        }

        List<TaskServiceResponseModel> foundTasks = taskRepository
                .findTasksByAccountIdAndFilters(
                        accountEntity.getAccountId(),
                        status.toLowerCase(),
                        priority.toLowerCase())
                .stream()
                .map(serviceResponseFactory::createGeneralResponse)
                .toList();

        return taskPresenter.prepareSuccessView(foundTasks);
    }
}