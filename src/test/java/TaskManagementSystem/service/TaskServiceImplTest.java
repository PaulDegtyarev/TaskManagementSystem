package TaskManagementSystem.service;

import TaskManagementSystem.config.MyUserDetails;
import TaskManagementSystem.dataStore.impl.TaskDSImpl;
import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.TaskDBO;
import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.entity.PriorityEntity;
import TaskManagementSystem.entity.RoleEntity;
import TaskManagementSystem.entity.TaskEntity;
import TaskManagementSystem.exception.task.TaskBadRequestException;
import TaskManagementSystem.exception.task.TaskForbiddenException;
import TaskManagementSystem.exception.task.TaskNotFoundException;
import TaskManagementSystem.presenter.impl.TaskFormatter;
import TaskManagementSystem.repository.AccountRepository;
import TaskManagementSystem.repository.PriorityRepository;
import TaskManagementSystem.repository.StatusRepository;
import TaskManagementSystem.repository.TaskRepository;
import TaskManagementSystem.service.impl.TaskServiceImpl;
import TaskManagementSystem.util.RoleUtil;
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
    private TaskDSImpl taskDS;

    private BindingResult bindingResult;
    private TaskDBO dto;
    private GeneralTaskDSResponseModel expectedResponse;
    private AccountEntity authorEntity;
    private AccountEntity executorEntity;
    private TaskEntity taskEntity;
    private Integer taskId;
    private Integer authorId;
    private SecurityContext securityContext;
    private Authentication authentication;
    private MyUserDetails myUserDetails;
    private Integer accountId;


    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        bindingResult = mock(BindingResult.class);

        dto = new TaskDBO(
                "Подписать бумажку",
                "Купить бумагу в магазине, подписать ее ручкой",
                "Высокий",
                3,
                4,
                "Перед сдачей задачи напишите мне или позвоните"
        );

        authorId = 3;

        authorEntity = new AccountEntity(
                "authorEmail@author.ru",
                "author",
                "authorFirstname",
                "authorLastname",
                1
        );
        authorEntity.setRoleEntity(new RoleEntity(RoleUtil.AUTHOR_En));
        authorEntity.setAccountId(authorId);

        executorEntity = new AccountEntity(
                "executor@executor.ru",
                "executor",
                "executorFirstname",
                "executorLastname",
                2
        );
        executorEntity.setRoleEntity(new RoleEntity(RoleUtil.EXECUTOR_En));

        taskId = 2;

        expectedResponse = new GeneralTaskDSResponseModel(
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


        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        authentication = mock(Authentication.class);
        myUserDetails = mock(MyUserDetails.class);
        accountId = dto.getAuthorId();
    }

    @Test
    @DisplayName("Успешный тест создания задачи")
    void createTaskSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(accountRepository.findById(dto.getExecutorId())).thenReturn(Optional.of(executorEntity));
        when(priorityRepository.findByPriority(dto.getPriority().toLowerCase())).thenReturn(Optional.of(new PriorityEntity()));
        when(taskDS.createTask(dto)).thenReturn(expectedResponse);
        when(taskService.createTask(dto, bindingResult)).thenReturn(expectedResponse);

        GeneralTaskDSResponseModel actualResponse = taskService.createTask(dto, bindingResult);

        Assertions.assertEquals(expectedResponse, actualResponse);
        verify(taskPresenter).prepareSuccessView(expectedResponse);
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом, если в данных есть ошибка")
    void createTaskShouldReturnBadRequestExceptionForWrongData() {
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
        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.empty());
        when(taskPresenter.prepareNotFoundView("Автор не найден")).thenReturn(new TaskNotFoundException("Автор не найден"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.createTask(dto, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView("Автор не найден");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если исполнитель не существует")
    void createTaskShouldReturnNotFoundExceptionForNonExistenceExecutor() {
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
        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));

        when(accountRepository.findById(dto.getExecutorId())).thenReturn(Optional.of(executorEntity));
        when(priorityRepository.findByPriority(dto.getPriority().toLowerCase())).thenReturn(Optional.of(new PriorityEntity()));
        when(taskDS.updateTaskById(taskId, dto)).thenReturn(expectedResponse);

        when(taskService.updateTaskById(taskId, dto, bindingResult)).thenReturn(expectedResponse);

        GeneralTaskDSResponseModel actualResponse = taskService.updateTaskById(taskId, dto, bindingResult);

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
                () -> taskService.updateTaskById(taskId, dto, bindingResult)
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
                () -> taskService.updateTaskById(taskId, dto, bindingResult)
        );

        verify(taskPresenter).prepareBadRequestView("Неверные входные данные");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если автор не существует")
    void updateTaskByIdShouldReturnNotFoundExceptionForNonExistenceAuthor() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(new TaskEntity()));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.empty());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);
        when(taskPresenter.prepareNotFoundView("Автор не найден")).thenThrow(new TaskNotFoundException("Автор не найден"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTaskById(taskId, dto, bindingResult)
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

        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));

        when(taskPresenter.prepareForbiddenView("Исполнитель не может быть автором")).thenThrow(new TaskForbiddenException("Исполнитель не может быть автором"));

        Assertions.assertThrows(
                TaskForbiddenException.class,
                () -> taskService.updateTaskById(taskId, dto, bindingResult)
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

        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));

        System.out.println(!taskEntity
                .getAuthorEntity()
                .getAccountId()
                .equals(accountId));

        when(taskPresenter.prepareForbiddenView(anyString())).thenThrow(new TaskForbiddenException("Вы не можете обновить чужую задач"));

        Assertions.assertThrows(
                TaskForbiddenException.class,
                ()-> taskService.updateTaskById(taskId, dto, bindingResult));

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

        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(accountRepository.findById(dto.getExecutorId())).thenReturn(Optional.empty());

        when(taskPresenter.prepareNotFoundView("Исполнитель не найден")).thenReturn(new TaskNotFoundException("Исполнитель не найден"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTaskById(taskId, dto, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView("Исполнитель не найден");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом, если роль исполнителя не ROLE_EXECUTOR")
    void updateTaskByIdShouldReturnForbiddenExceptionForExecutorWithWrongRole() {
        executorEntity.setRoleEntity(new RoleEntity(RoleUtil.AUTHOR_En));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);
        when(accountRepository.findById(dto.getExecutorId())).thenReturn(Optional.of(executorEntity));

        when(taskPresenter.prepareForbiddenView(anyString())).thenThrow(new TaskForbiddenException("Автор не может быть исполнителем"));

        Assertions.assertThrows(
                TaskForbiddenException.class,
                () -> taskService.updateTaskById(taskId, dto, bindingResult)
        );

        verify(taskPresenter).prepareForbiddenView("Автор не может быть исполнителем");
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом, если приоритет не существует")
    void updateTaskByIdShouldReturnNotFoundExceptionForNonExistencePriority() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(accountRepository.findById(dto.getAuthorId())).thenReturn(Optional.of(authorEntity));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);
        when(accountRepository.findById(dto.getExecutorId())).thenReturn(Optional.of(executorEntity));
        when(priorityRepository.findByPriority(dto.getPriority().toLowerCase())).thenReturn(Optional.empty());

        when(taskPresenter.prepareNotFoundView("Приоритет не найден")).thenThrow(new TaskNotFoundException("Приоритет не найден"));

        Assertions.assertThrows(
                TaskNotFoundException.class,
                () -> taskService.updateTaskById(taskId, dto, bindingResult)
        );

        verify(taskPresenter).prepareNotFoundView("Приоритет не найден");
    }

    @Test
    @DisplayName("Успешный тест просмотра всех своих задач")
    void getAllTasksFromAuthorSuccess() {
        List<GeneralTaskDSResponseModel> expectedResponse = List.of(
                new GeneralTaskDSResponseModel(),
                new GeneralTaskDSResponseModel(),
                new GeneralTaskDSResponseModel()
        );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(myUserDetails);
        when(myUserDetails.getId()).thenReturn(accountId);
        when(taskDS.getAllTasksByAuthorId(accountId)).thenReturn(expectedResponse);
        when(taskPresenter.prepareSuccessView(expectedResponse)).thenReturn(expectedResponse);

        List<GeneralTaskDSResponseModel> actualResponse = taskService.getAllTasksFromAuthor();

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

        GeneralTaskDSResponseModel actualResponse = taskService.getTaskByTaskId(taskId);

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
}