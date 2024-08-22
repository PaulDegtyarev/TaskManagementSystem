package TaskManagementSystem.service;

import TaskManagementSystem.config.MyUserDetails;
import TaskManagementSystem.dataStore.impl.TaskDSImpl;
import TaskManagementSystem.dto.dSRequest.TaskDSRequestModel;
import TaskManagementSystem.dto.serviceResponse.TaskServiceResponseModel;
import TaskManagementSystem.dto.dbo.GeneralTaskDBO;
import TaskManagementSystem.dto.dbo.StatusDBO;
import TaskManagementSystem.dto.dbo.TaskDBOToUpdateTaskByTaskId;
import TaskManagementSystem.entity.*;
import TaskManagementSystem.exception.task.TaskBadRequestException;
import TaskManagementSystem.exception.task.TaskForbiddenException;
import TaskManagementSystem.exception.task.TaskNotFoundException;
import TaskManagementSystem.factory.impl.ServiceResponseFactoryImpl;
import TaskManagementSystem.presenter.impl.TaskFormatter;
import TaskManagementSystem.repository.AccountRepository;
import TaskManagementSystem.repository.PriorityRepository;
import TaskManagementSystem.repository.StatusRepository;
import TaskManagementSystem.repository.TaskRepository;
import TaskManagementSystem.service.impl.TaskServiceImpl;
import TaskManagementSystem.util.RoleUtil;
import TaskManagementSystem.util.StatusUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class TaskServiceImplTest {
    @Mock
    private TaskFormatter taskPresenter;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PriorityRepository priorityRepository;

    @Mock
    private ServiceResponseFactoryImpl dsResponseFactory;

    @Mock
    private TaskDSImpl taskDS;

    private BindingResult bindingResult;
    private GeneralTaskDBO dto;
    private TaskServiceResponseModel expectedResponse;
    private AccountEntity authorEntity;
    private AccountEntity executorEntity;
    private TaskEntity taskEntity;
    private TaskDSRequestModel dsRequest;
    private Integer taskId;
    private TaskDBOToUpdateTaskByTaskId dboToUpdateTaskByTaskId;
    private StatusEntity statusEntity;
    private Integer authorId;
    private SecurityContext securityContext;
    private Authentication authentication;
    private MyUserDetails myUserDetails;
    private Integer accountId;
    private StatusDBO statusDBO;
    private Integer executorId;
    private String status;
    private String priority;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        bindingResult = mock(BindingResult.class);

        dto = new GeneralTaskDBO(
                "Подписать бумажку",
                "Купить бумагу в магазине, подписать ее ручкой",
                "Высокий",
                3,
                4,
                "Перед сдачей задачи напишите мне или позвоните"
        );

        authorId = dto.getAuthorId();

        authorEntity = new AccountEntity(
                "authorEmail@author.ru",
                "author",
                "authorFirstname",
                "authorLastname",
                1
        );
        authorEntity.setRoleEntity(new RoleEntity(RoleUtil.AUTHOR_En));
        authorEntity.setAccountId(authorId);

        executorId = dto.getExecutorId();

        executorEntity = new AccountEntity(
                "executor@executor.ru",
                "executor",
                "executorFirstname",
                "executorLastname",
                2
        );
        executorEntity.setRoleEntity(new RoleEntity(RoleUtil.EXECUTOR_En));
        executorEntity.setAccountId(executorId);

        dsRequest = new TaskDSRequestModel(
                dto.getTitle(),
                dto.getDescription(),
                dto.getPriority(),
                dto.getAuthorId(),
                dto.getExecutorId(),
                dto.getComment(),
                StatusUtil.WAITING
        );

        taskId = 2;

        dboToUpdateTaskByTaskId = new TaskDBOToUpdateTaskByTaskId(
                 "Подписать бумажку",
                 "Купить бумагу в магазине, подписать ее ручкой",
                 "Высокий",
                 "В процессе",
                 3,
                 4,
                 "Перед сдачей задачи напишите мне или позвоните"
        );

        statusEntity = new StatusEntity(
                dboToUpdateTaskByTaskId.getStatus()
        );

        expectedResponse = new TaskServiceResponseModel(
                2,
                dto.getTitle(),
                dto.getDescription(),
                "в ожидании",
                dto.getPriority().toLowerCase(),
                "author@author.ru",
                executorEntity.getEmail(),
                dto.getComment()
        );

        taskEntity = new TaskEntity(
                dto.getTitle(),
                dto.getDescription(),
                1,
                3,
                authorId,
                2,
                dto.getComment()
        );
        taskEntity.setAuthorEntity(authorEntity);
        taskEntity.setExecutorEntity(executorEntity);

        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        authentication = mock(Authentication.class);
        myUserDetails = mock(MyUserDetails.class);
        accountId = dto.getAuthorId();

        statusDBO = new StatusDBO(
            "В процессе"
        );

        status = statusDBO.getStatus();
        priority = dto.getPriority();
    }

    @Test
    @DisplayName("Успешный тест создания задачи")
    void createTaskSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(accountRepository.findById(dto.getExecutorId())).thenReturn(Optional.of(executorEntity));
        when(priorityRepository.findByPriority(dto.getPriority().toLowerCase())).thenReturn(Optional.of(new PriorityEntity()));
        when(taskDS.createTask(any(TaskDSRequestModel.class))).thenReturn(expectedResponse);
        when(taskService.createTask(dto, bindingResult)).thenReturn(expectedResponse);

        TaskServiceResponseModel actualResponse = taskService.createTask(dto, bindingResult);

        Assertions.assertEquals(expectedResponse, actualResponse);
        verify(taskPresenter).prepareSuccessView(expectedResponse);
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом, если в данных есть ошибка")
    void createTaskShouldReturnBadRequestExceptionForWrongData() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(taskPresenter.prepareBadRequestView(anyString())).thenReturn(new TaskBadRequestException("Неверные входные данные"));

        Assertions.assertThrows(
                TaskBadRequestException.class,
                () -> taskService.createTask(dto, bindingResult)
        );

        verify(taskPresenter).prepareBadRequestView("Неверные входные данные");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если автор не существует")
    void createTaskShouldReturnNotFoundExceptionForNonExistenceAuthor() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.empty());
        when(taskPresenter.prepareNotFoundView("Автор не найден")).thenReturn(new TaskNotFoundException("Автор не найден"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.createTask(dto, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView("Автор не найден");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом, если authorId из dto != accountId того, кто кидал запрос")
    void createTaskShouldReturnBadRequestExceptionForWrongAuthorId() {
        accountId = 1;
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(taskPresenter.prepareBadRequestView(anyString())).thenThrow(new TaskBadRequestException("id автора не совпадает с Вашим"));

        Assertions.assertThrows(
                TaskBadRequestException.class,
                () -> taskService.createTask(dto, bindingResult)
        );

        verify(taskPresenter).prepareBadRequestView("id автора не совпадает с Вашим");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если исполнитель не существует")
    void createTaskShouldReturnNotFoundExceptionForNonExistenceExecutor() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(accountRepository.findById(dto.getExecutorId())).thenReturn(Optional.empty());
        when(taskPresenter.prepareNotFoundView("Исполнитель не найден")).thenReturn(new TaskNotFoundException("Исполнитель не найден"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.createTask(dto, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView("Исполнитель не найден");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом, если у аккаунта, указанного исполнителем, роль не ROLE_EXECUTOR")
    void createTaskShouldReturnForbiddenExceptionForWrongExecutorRole() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        executorEntity.setRoleEntity(new RoleEntity(RoleUtil.AUTHOR_En));

        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(accountRepository.findById(dto.getExecutorId())).thenReturn(Optional.of(executorEntity));

        when(taskPresenter.prepareForbiddenView("Автор не может быть исполнителем")).thenReturn(new TaskForbiddenException("Автор не может быть исполнителем"));

        Assertions.assertThrows(
                TaskForbiddenException.class,
                () -> taskService.createTask(dto, bindingResult)
        );

        verify(taskPresenter).prepareForbiddenView("Автор не может быть исполнителем");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом, если у аккаунта, указанного автором, роль не ROLE_AUTHOR")
    void createTaskShouldReturnForbiddenExceptionForWrongAuthorRole() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        authorEntity.setRoleEntity(new RoleEntity(RoleUtil.EXECUTOR_En));

        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(taskPresenter.prepareForbiddenView("Исполнитель не может быть автором")).thenReturn(new TaskForbiddenException("Исполнитель не может быть автором"));

        Assertions.assertThrows(
                TaskForbiddenException.class,
                () -> taskService.createTask(dto, bindingResult)
        );

        verify(taskPresenter).prepareForbiddenView("Исполнитель не может быть автором");

    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если приоритет не существует")
    void createTaskShouldReturnNotFoundExceptionForNonExistencePriority() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(accountRepository.findById(dto.getExecutorId())).thenReturn(Optional.of(executorEntity));
        when(priorityRepository.findByPriority(dto.getPriority().toLowerCase())).thenReturn(Optional.empty());
        when(taskPresenter.prepareNotFoundView("Приоритет не найден")).thenReturn(new TaskNotFoundException("Приоритет не найден"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.createTask(dto, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView("Приоритет не найден");
    }

    @Test
    @DisplayName("Успешный тест обновления задачи по taskId")
    void updateTaskByIdSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountRepository.findById(dboToUpdateTaskByTaskId.getAuthorId())).thenReturn(Optional.of(authorEntity));

        when(accountRepository.findById(dboToUpdateTaskByTaskId.getExecutorId())).thenReturn(Optional.of(executorEntity));
        when(priorityRepository.findByPriority(dboToUpdateTaskByTaskId.getPriority().toLowerCase())).thenReturn(Optional.of(new PriorityEntity()));
        when(statusRepository.findByStatus(dboToUpdateTaskByTaskId.getStatus().toLowerCase())).thenReturn(Optional.of(statusEntity));
        when(taskDS.updateTaskById(taskId, dboToUpdateTaskByTaskId)).thenReturn(expectedResponse);

        when(taskService.updateTaskById(taskId, dboToUpdateTaskByTaskId, bindingResult)).thenReturn(expectedResponse);

        TaskServiceResponseModel actualResponse = taskService.updateTaskById(taskId, dboToUpdateTaskByTaskId, bindingResult);

        Assertions.assertEquals(expectedResponse, actualResponse);
        verify(taskPresenter).prepareSuccessView(expectedResponse);
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если задача не существует")
    void updateTaskByIdShouldReturnNotFoundExceptionForNonExistenceTask() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        when(taskPresenter.prepareNotFoundView("Задача не найдена")).thenReturn(new TaskNotFoundException("Задача не найдена"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTaskById(taskId, dboToUpdateTaskByTaskId, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView("Задача не найдена");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом, если в данных есть ошибка")
    void updateTaskByIdShouldReturnBadRequestException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(new TaskEntity()));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(taskPresenter.prepareBadRequestView("Неверные входные данные")).thenThrow(new TaskBadRequestException("Неверные входные данные"));

        Assertions.assertThrows(
                TaskBadRequestException.class,
                () -> taskService.updateTaskById(taskId, dboToUpdateTaskByTaskId, bindingResult)
        );

        verify(taskPresenter).prepareBadRequestView("Неверные входные данные");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если автор не существует")
    void updateTaskByIdShouldReturnNotFoundExceptionForNonExistenceAuthor() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(new TaskEntity()));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountRepository.findById(dboToUpdateTaskByTaskId.getAuthorId())).thenReturn(Optional.empty());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);
        when(taskPresenter.prepareNotFoundView("Автор не найден")).thenThrow(new TaskNotFoundException("Автор не найден"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTaskById(taskId, dboToUpdateTaskByTaskId, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView("Автор не найден");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом, если роль автора не ROLE_AUTHOR")
    void updateTaskByIdShouldReturnForbiddenExceptionForAuthorWithWrongRole() {
        authorEntity.setRoleEntity(new RoleEntity(RoleUtil.EXECUTOR_En));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(bindingResult.hasErrors()).thenReturn(false);

        when(accountRepository.findById(dboToUpdateTaskByTaskId.getAuthorId())).thenReturn(Optional.of(authorEntity));

        when(taskPresenter.prepareForbiddenView("Исполнитель не может быть автором")).thenThrow(new TaskForbiddenException("Исполнитель не может быть автором"));

        Assertions.assertThrows(
                TaskForbiddenException.class,
                () -> taskService.updateTaskById(taskId, dboToUpdateTaskByTaskId, bindingResult)
        );

        verify(taskPresenter).prepareForbiddenView("Исполнитель не может быть автором");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом, если пользователь обновляет не свою задачу")
    void updateTaskByTaskIdShouldReturnBadRequestException() {
        accountId = 1;
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(bindingResult.hasErrors()).thenReturn(false);

        when(accountRepository.findById(dboToUpdateTaskByTaskId.getAuthorId())).thenReturn(Optional.of(authorEntity));

        System.out.println(!taskEntity
                .getAuthorEntity()
                .getAccountId()
                .equals(accountId));

        when(taskPresenter.prepareForbiddenView(anyString())).thenThrow(new TaskForbiddenException("Вы не можете обновить чужую задач"));

        Assertions.assertThrows(
                TaskForbiddenException.class,
                ()-> taskService.updateTaskById(taskId, dboToUpdateTaskByTaskId, bindingResult));

        verify(taskPresenter).prepareForbiddenView("Вы не можете обновить чужую задачу");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если исполнитель не существует")
    void updateTaskByIdShouldReturnNotFoundExceptionForNonExistenceExecutor() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(bindingResult.hasErrors()).thenReturn(false);

        when(accountRepository.findById(dboToUpdateTaskByTaskId.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(accountRepository.findById(dboToUpdateTaskByTaskId.getExecutorId())).thenReturn(Optional.empty());

        when(taskPresenter.prepareNotFoundView("Исполнитель не найден")).thenReturn(new TaskNotFoundException("Исполнитель не найден"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTaskById(taskId, dboToUpdateTaskByTaskId, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView("Исполнитель не найден");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом, если роль исполнителя не ROLE_EXECUTOR")
    void updateTaskByIdShouldReturnForbiddenExceptionForExecutorWithWrongRole() {
        executorEntity.setRoleEntity(new RoleEntity(RoleUtil.AUTHOR_En));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountRepository.findById(dboToUpdateTaskByTaskId.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);
        when(accountRepository.findById(dboToUpdateTaskByTaskId.getExecutorId())).thenReturn(Optional.of(executorEntity));

        when(taskPresenter.prepareForbiddenView(anyString())).thenThrow(new TaskForbiddenException("Автор не может быть исполнителем"));

        Assertions.assertThrows(
                TaskForbiddenException.class,
                () -> taskService.updateTaskById(taskId, dboToUpdateTaskByTaskId, bindingResult)
        );

        verify(taskPresenter).prepareForbiddenView("Автор не может быть исполнителем");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если приоритет не существует")
    void updateTaskByIdShouldReturnNotFoundExceptionForNonExistencePriority() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountRepository.findById(dboToUpdateTaskByTaskId.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);
        when(accountRepository.findById(dboToUpdateTaskByTaskId.getExecutorId())).thenReturn(Optional.of(executorEntity));
        when(priorityRepository.findByPriority(dboToUpdateTaskByTaskId.getPriority().toLowerCase())).thenReturn(Optional.empty());

        when(taskPresenter.prepareNotFoundView("Приоритет не найден")).thenThrow(new TaskNotFoundException("Приоритет не найден"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTaskById(taskId, dboToUpdateTaskByTaskId, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView("Приоритет не найден");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если статус не существует")
    void updateTaskByIdShouldReturnNotFoundExceptionForNonExistenceStatus() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountRepository.findById(dboToUpdateTaskByTaskId.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);
        when(accountRepository.findById(dboToUpdateTaskByTaskId.getExecutorId())).thenReturn(Optional.of(executorEntity));
        when(priorityRepository.findByPriority(dboToUpdateTaskByTaskId.getPriority().toLowerCase())).thenReturn(Optional.of(new PriorityEntity()));
        when(statusRepository.findByStatus(dboToUpdateTaskByTaskId.getStatus().toLowerCase())).thenReturn(Optional.empty());

        when(taskPresenter.prepareNotFoundView("Статус не найден")).thenThrow(new TaskNotFoundException("Статус не найден"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTaskById(taskId, dboToUpdateTaskByTaskId, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView("Статус не найден");
    }

    @Test
    @DisplayName("Успешный тест просмотра всех своих задач")
    void getAllTasksFromAuthorSuccess() {
        List<TaskServiceResponseModel> expectedResponse = List.of(
                new TaskServiceResponseModel(),
                new TaskServiceResponseModel(),
                new TaskServiceResponseModel()
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);
        when(taskDS.getAllTasksByAuthorId(accountId)).thenReturn(expectedResponse);
        when(taskPresenter.prepareSuccessView(expectedResponse)).thenReturn(expectedResponse);

        List<TaskServiceResponseModel> actualResponse = taskService.getAllTasksFromAuthor();

        Assertions.assertEquals(expectedResponse, actualResponse);
        verify(taskPresenter).prepareSuccessView(expectedResponse);
    }

    @Test
    @DisplayName("Успешный тест поиска своей задачи по taskId")
    void getTaskByTaskIdSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskDS.getTaskByTaskId(taskId)).thenReturn(expectedResponse);
        when(taskPresenter.prepareSuccessView(expectedResponse)).thenReturn(expectedResponse);

        TaskServiceResponseModel actualResponse = taskService.getTaskByTaskId(taskId);

        Assertions.assertEquals(expectedResponse, actualResponse);

        verify(taskPresenter).prepareSuccessView(expectedResponse);
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если задача не существует")
    void getTaskByTaskIdShouldReturnNotFoundExceptionForNonExistenceTask() {
        taskId = 10;
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        when(taskPresenter.prepareNotFoundView(anyString())).thenThrow(new TaskNotFoundException("Задача не найдена"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTaskByTaskId(taskId)
        );

        verify(taskPresenter).prepareNotFoundView("Задача не найдена");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом, если у автора нет такой задачи")
    void getTaskByTaskIdShouldReturnNotFoundException() {
        accountId = 5;

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(taskPresenter.prepareBadRequestView(anyString())).thenThrow(new TaskBadRequestException("У Вас нет такой задачи"));

        Assertions.assertThrows(
                TaskBadRequestException.class,
                () -> taskService.getTaskByTaskId(taskId)
        );

        verify(taskPresenter).prepareBadRequestView("У Вас нет такой задачи");
    }

    @Test
    @DisplayName("Успешный тест удаления задачи по taskId")
    void deleteTaskByTaskIdSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        taskService.deleteTaskByTaskId(taskId);

        verify(taskRepository, times(1)).delete(taskEntity);
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом для несуществующей задачи")
    void deleteTaskByTaskIdShouldReturnNotFoundExceptionForNonExistenceTask() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        TaskNotFoundException taskNotFoundException = new TaskNotFoundException("Задача не найдена");

        when(taskRepository.findById(taskId)).thenThrow(taskNotFoundException);
        when(taskPresenter.prepareNotFoundView(anyString())).thenThrow(taskNotFoundException);

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.deleteTaskByTaskId(taskId)
        );

        verify(taskPresenter).prepareNotFoundView(taskNotFoundException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом, если пользователь удаляет чужую задачу")
    void deleteTaskByTaskIdShouldReturnBadRequestException() {
        accountId = 10;

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        TaskForbiddenException taskForbiddenException = new TaskForbiddenException("Вы не можете удалить чужую задачу");

        when(taskPresenter.prepareForbiddenView(anyString())).thenThrow(taskForbiddenException);

        Assertions.assertThrows(
                TaskForbiddenException.class,
                () -> taskService.deleteTaskByTaskId(taskId)
        );

        verify(taskPresenter).prepareForbiddenView(taskForbiddenException.getMessage());
    }

    @Test
    @DisplayName("Успешный тест обновления статуса у задачи")
    void updateStatusOfTaskByTaskIdForAuthorSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(statusRepository.findByStatus(statusDBO.getStatus().toLowerCase())).thenReturn(Optional.of(statusEntity));
        when(dsResponseFactory.createGeneralResponse(taskEntity)).thenReturn(expectedResponse);
        when(taskPresenter.prepareSuccessView(expectedResponse)).thenReturn(expectedResponse);

        TaskServiceResponseModel actualResponse = taskService.updateStatusOfTaskByTaskIdForAuthor(taskId, statusDBO, bindingResult);

        Assertions.assertEquals(expectedResponse, actualResponse);

        verify(dsResponseFactory).createGeneralResponse(taskEntity);
        verify(taskPresenter).prepareSuccessView(expectedResponse);
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом для несуществующей задачи")
    void updateStatusOfTaskByTaskIdShouldReturnNotFoundExceptionForNonExistenceTaskForAuthor() {
        taskId = 20;

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        TaskNotFoundException taskNotFoundException = new TaskNotFoundException("Задача не найдена");

        when(taskPresenter.prepareNotFoundView(anyString())).thenThrow(taskNotFoundException);

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateStatusOfTaskByTaskIdForAuthor(taskId, statusDBO, bindingResult));

        verify(taskPresenter).prepareNotFoundView(taskNotFoundException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом, если пользователь обновляет статусой чужой задачи")
    void updateStatusOfTaskByTaskIdForAuthorShouldReturnForbiddenException() {
        accountId = 1;

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        TaskForbiddenException taskForbiddenException = new TaskForbiddenException("Вы не можете обновить статус чужой задачи");

        when(taskPresenter.prepareForbiddenView(anyString())).thenThrow(taskForbiddenException);

        Assertions.assertThrows(
                TaskForbiddenException.class,
                () -> taskService.updateStatusOfTaskByTaskIdForAuthor(taskId, statusDBO, bindingResult));

        verify(taskPresenter).prepareForbiddenView(taskForbiddenException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом при пустом статусе")
    void updateStatusOfTaskByTaskIdForAuthorShouldReturnBadRequestException() {
        statusDBO.setStatus("     ");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(bindingResult.hasErrors()).thenReturn(true);

        TaskBadRequestException taskBadRequestException = new TaskBadRequestException("Неверно введенные данные");

        when(taskPresenter.prepareBadRequestView(anyString())).thenThrow(taskBadRequestException);

        Assertions.assertThrows(
                TaskBadRequestException.class,
                () -> taskService.updateStatusOfTaskByTaskIdForAuthor(taskId, statusDBO, bindingResult));

        verify(taskPresenter).prepareBadRequestView(taskBadRequestException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если статус не существует")
    void updateStatusOfTaskByTaskIdShouldReturnNotFoundExceptionForNonExistenceStatusForAuthor() {
        statusDBO.setStatus("Делается");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(statusRepository.findByStatus(statusDBO.getStatus().toLowerCase())).thenReturn(Optional.empty());

        TaskNotFoundException taskNotFoundException = new TaskNotFoundException("Статус не найден");

        when(taskPresenter.prepareNotFoundView(anyString())).thenThrow(taskNotFoundException);

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateStatusOfTaskByTaskIdForAuthor(taskId, statusDBO, bindingResult));

        verify(taskPresenter).prepareNotFoundView(taskNotFoundException.getMessage());
    }

    @Test
    @DisplayName("Успешный тест обновления исполнителя у задачи по taskId и executorId")
    void updateExecutorOfTaskByTaskIdSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(accountRepository.findById(executorId)).thenReturn(Optional.of(executorEntity));
        when(dsResponseFactory.createGeneralResponse(taskEntity)).thenReturn(expectedResponse);
        when(taskPresenter.prepareSuccessView(expectedResponse)).thenReturn(expectedResponse);

        TaskServiceResponseModel actualResponse = taskService.updateExecutorOfTaskByTaskId(taskId, executorId);

        Assertions.assertEquals(expectedResponse, actualResponse);

        verify(dsResponseFactory).createGeneralResponse(taskEntity);
        verify(taskPresenter).prepareSuccessView(expectedResponse);
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если задача не существует")
    void updateExecutorOfTaskByTaskIdShouldReturnNotFoundExceptionForNonExistenceTask() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        TaskNotFoundException taskNotFoundException = new TaskNotFoundException("Задача не найдена");
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        when(taskPresenter.prepareNotFoundView(anyString())).thenThrow(taskNotFoundException);

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateExecutorOfTaskByTaskId(taskId, executorId)
        );

        verify(taskPresenter).prepareNotFoundView(taskNotFoundException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом, если пользователь обновляет не свою задачу")
    void updateExecutorOfTaskByTaskIdShouldReturnForbiddenException() {
        accountId = 5;

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        TaskForbiddenException taskForbiddenException = new TaskForbiddenException("Вы не можете обновить исполнителя чужой задачи");
        when(taskPresenter.prepareForbiddenView(anyString())).thenThrow(taskForbiddenException);

        Assertions.assertThrows(
                TaskForbiddenException.class,
                () -> taskService.updateExecutorOfTaskByTaskId(taskId, executorId)
        );

        verify(taskPresenter).prepareForbiddenView(taskForbiddenException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если исполнитель не существует")
    void updateExecutorOfTaskByTaskIdShouldReturnNotFoundExceptionForNonExistenceExecutor() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        TaskNotFoundException taskNotFoundException = new TaskNotFoundException("Исполнитель не найден");

        when(accountRepository.findById(executorId)).thenReturn(Optional.empty());
        when(taskPresenter.prepareNotFoundView(anyString())).thenThrow(taskNotFoundException);

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateExecutorOfTaskByTaskId(taskId, executorId)
        );

        verify(taskPresenter).prepareNotFoundView(taskNotFoundException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом, если роль исполнителя != ROLE_EXECUTOR")
    void updateExecutorOfTaskByTaskIdShouldReturnBadRequestException() {
        executorEntity.setRoleEntity(new RoleEntity(RoleUtil.AUTHOR_En));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(accountRepository.findById(executorId)).thenReturn(Optional.of(executorEntity));

        TaskBadRequestException taskBadRequestException = new TaskBadRequestException("Автор не может быть исполнителем");
        when(taskPresenter.prepareBadRequestView(anyString())).thenThrow(taskBadRequestException);

        Assertions.assertThrows(
                TaskBadRequestException.class,
                () -> taskService.updateExecutorOfTaskByTaskId(taskId, executorId)
        );

        verify(taskPresenter).prepareBadRequestView(taskBadRequestException.getMessage());
    }

    @Test
    @DisplayName("Успешный тест обновления статуса у задачи")
    void updateStatusOfTaskByTaskIdForExecutorSuccess() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(executorId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(statusRepository.findByStatus(statusDBO.getStatus().toLowerCase())).thenReturn(Optional.of(statusEntity));
        when(dsResponseFactory.createGeneralResponse(taskEntity)).thenReturn(expectedResponse);
        when(taskPresenter.prepareSuccessView(expectedResponse)).thenReturn(expectedResponse);

        TaskServiceResponseModel actualResponse = taskService.updateStatusOfTaskByTaskIdForExecutor(taskId, statusDBO, bindingResult);

        Assertions.assertEquals(expectedResponse, actualResponse);

        verify(dsResponseFactory).createGeneralResponse(taskEntity);
        verify(taskPresenter).prepareSuccessView(expectedResponse);
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если задача не существует")
    void updateStatusOfTaskByTaskIdForExecutorShouldReturnNotFoundExceptionForNonExistenceTask() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(executorId);

        TaskNotFoundException taskNotFoundException = new TaskNotFoundException("Задача не найена");
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        when(taskPresenter.prepareNotFoundView(anyString())).thenThrow(taskNotFoundException);

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateStatusOfTaskByTaskIdForExecutor(taskId, statusDBO, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView(taskNotFoundException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом, если пользователь обновляет чужую задачу")
    void updateStatusOfTaskByTaskIdForExecutorShouldReturnForbiddenException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        TaskForbiddenException taskForbiddenException = new TaskForbiddenException("Вы не можете обновить статус чужой задачи");

        when(taskPresenter.prepareForbiddenView(anyString())).thenThrow(taskForbiddenException);

        Assertions.assertThrows(
                TaskForbiddenException.class,
                () -> taskService.updateStatusOfTaskByTaskIdForExecutor(taskId, statusDBO, bindingResult)
        );

        verify(taskPresenter).prepareForbiddenView(taskForbiddenException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом, если ошибка в веденных данных")
    void updateStatusOfTaskByTaskIdForExecutorShouldBadRequestException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(executorId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        TaskBadRequestException taskBadRequestException = new TaskBadRequestException("Данные неверно введены");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(taskPresenter.prepareBadRequestView(anyString())).thenThrow(taskBadRequestException);

        Assertions.assertThrows(
                TaskBadRequestException.class,
                () -> taskService.updateStatusOfTaskByTaskIdForExecutor(taskId, statusDBO, bindingResult)
        );

        verify(taskPresenter).prepareBadRequestView(taskBadRequestException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если статус не существует")
    void updateStatusOfTaskByTaskIdForExecutorShouldReturnNotFoundExceptionForNonExistenceStatus() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(executorId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(statusRepository.findByStatus(statusDBO.getStatus().toLowerCase())).thenReturn(Optional.empty());

        TaskNotFoundException taskNotFoundException = new TaskNotFoundException("Статус не найден");

        when(taskPresenter.prepareNotFoundView(anyString())).thenThrow(taskNotFoundException);

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateStatusOfTaskByTaskIdForExecutor(taskId, statusDBO, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView(taskNotFoundException.getMessage());
    }

    @Test
    @DisplayName("Успешный тест поиска всех задач конкретного аккаунта")
    void getTasksByAccountIdAndFiltersSuccess() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(authorEntity));
        when(taskRepository.findTasksByAccountIdAndFilters(accountId, status.toLowerCase(), priority.toLowerCase())).thenReturn(List.of(taskEntity));
        when(dsResponseFactory.createGeneralResponse(taskEntity)).thenReturn(expectedResponse);
        when(taskPresenter.prepareSuccessView(List.of(expectedResponse))).thenReturn(List.of(expectedResponse));

        List<TaskServiceResponseModel> actualResponse = taskService.getTasksByAccountIdAndFilters(accountId, status.toLowerCase(), priority.toLowerCase());

        Assertions.assertEquals(List.of(expectedResponse), actualResponse);

        verify(dsResponseFactory).createGeneralResponse(taskEntity);
        verify(taskPresenter).prepareSuccessView(List.of(expectedResponse));
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если указанный аккаунт не существует")
    void getTasksByAccountIdAndFiltersShouldReturnNotFoundExceptionForNonExistenceAccount() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        TaskNotFoundException taskNotFoundException = new TaskNotFoundException("Аккаунт не найден");

        when(taskPresenter.prepareNotFoundView(anyString())).thenThrow(taskNotFoundException);

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTasksByAccountIdAndFilters(accountId, status.toLowerCase(), priority.toLowerCase())
        );

        verify(taskPresenter).prepareNotFoundView(taskNotFoundException.getMessage());
    }

}