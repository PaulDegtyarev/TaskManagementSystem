package TaskManagementSystem.controller;

import TaskManagementSystem.TaskManagementSystemApplication;
import TaskManagementSystem.config.SecurityConfig;
import TaskManagementSystem.dto.serviceResponse.TaskServiceResponseModel;
import TaskManagementSystem.dto.dbo.GeneralTaskDBO;
import TaskManagementSystem.dto.dbo.StatusDBO;
import TaskManagementSystem.dto.dbo.TaskDBOToUpdateTaskByTaskId;
import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.entity.RoleEntity;
import TaskManagementSystem.exception.task.TaskNotFoundException;
import TaskManagementSystem.service.impl.TaskServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = TaskManagementSystemApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SecurityConfig.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private AutoCloseable closeable;

    @MockBean
    private TaskServiceImpl taskService;

    @InjectMocks
    private TaskController taskController;

    private BindingResult bindingResult;
    private AccountEntity executorEntity;
    private GeneralTaskDBO dto;
    private TaskServiceResponseModel expectedResponse;
    private Integer taskId;
    private TaskDBOToUpdateTaskByTaskId dboToUpdateTaskByTaskId;
    private StatusDBO statusDBO;
    private Integer executorId;
    private String status;
    private String priority;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @BeforeEach
    public void initMVC() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void setUp() {
        bindingResult = mock(BindingResult.class);

        dto = new GeneralTaskDBO(
                "Подписать бумажку",
                "Купить бумагу в магазине, подписать ее ручкой",
                "Высокий",
                3,
                4,
                "Перед сдачей задачи напишите мне или позвоните"
        );

        executorId = dto.getExecutorId();
        executorEntity = new AccountEntity(
                "executor@executor.ru",
                "executor",
                "executorFirstName",
                "executorLastName",
                2
        );
        executorEntity.setRoleEntity(new RoleEntity("ROLE_EXECUTOR"));
        executorEntity.setAccountId(executorId);

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

        expectedResponse = new TaskServiceResponseModel(
                taskId,
                dto.getTitle(),
                dto.getDescription(),
                "в ожидании",
                dto.getPriority().toLowerCase(),
                "author@author.ru",
                executorEntity.getEmail(),
                dto.getComment()
        );

        statusDBO = new StatusDBO(
            "В процессе"
        );

        status = statusDBO.getStatus();
        priority = dto.getPriority();
    }

    @Test
    @DisplayName("Успешный тест создания задачи")
    @WithMockUser(roles = "AUTHOR")
    void createTaskSuccess() throws Exception {
        when(taskService.createTask(any(GeneralTaskDBO.class), any(BindingResult.class))).thenReturn(expectedResponse);

        String jsonDTO = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/task")
                        .content(jsonDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskId").value(expectedResponse.getTaskId()))
                .andExpect(jsonPath("$.title").value(expectedResponse.getTitle()))
                .andExpect(jsonPath("$.description").value(expectedResponse.getDescription()))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus()))
                .andExpect(jsonPath("$.priority").value(expectedResponse.getPriority()))
                .andExpect(jsonPath("$.author").value(expectedResponse.getAuthor()))
                .andExpect(jsonPath("$.executor").value(expectedResponse.getExecutor()))
                .andExpect(jsonPath("$.comment").value(expectedResponse.getComment()))
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 401-ым статусом для неавторизованного аккаунта")
    @WithAnonymousUser
    void createTaskShouldReturnUnAuthorizeException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/task"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом для пользователя с ролью ROLE_EXECUTOR")
    @WithMockUser(roles = "EXECUTOR")
    void createTaskShouldReturnForbiddenExceptionForExecutor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/task"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Успешный тест обновления задачи по id")
    @WithMockUser(roles = "AUTHOR")
    void updateTaskByTaskIdSuccess() throws Exception {
        when(taskService.updateTaskById(eq(taskId), any(TaskDBOToUpdateTaskByTaskId.class), any(BindingResult.class))).thenReturn(expectedResponse);

        String jsonDTO = objectMapper.writeValueAsString(dboToUpdateTaskByTaskId);

        mockMvc.perform(MockMvcRequestBuilders.put("/task/me/{taskId}", taskId)
                        .content(jsonDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(expectedResponse.getTaskId()))
                .andExpect(jsonPath("$.title").value(expectedResponse.getTitle()))
                .andExpect(jsonPath("$.description").value(expectedResponse.getDescription()))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus()))
                .andExpect(jsonPath("$.priority").value(expectedResponse.getPriority()))
                .andExpect(jsonPath("$.author").value(expectedResponse.getAuthor()))
                .andExpect(jsonPath("$.executor").value(expectedResponse.getExecutor()))
                .andExpect(jsonPath("$.comment").value(expectedResponse.getComment()))
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом, если taskId <= 0 или null")
    @WithMockUser(roles = "AUTHOR")
    void updateTaskByTaskIdShouldReturnBadRequestExceptionForWrongTaskId() throws Exception {
        taskId = -1;

        mockMvc.perform(MockMvcRequestBuilders.put("/task/me/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 401-ым статусом для неавторизованного аккаунта")
    @WithAnonymousUser
    void updateTaskByTaskIdShouldReturnUnAuthorizeException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/task/me/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом для пользователя с ролью ROLE_EXECUTOR")
    @WithMockUser(roles = "EXECUTOR")
    void updateTaskByTaskIdShouldReturnForbiddenExceptionForExecutor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/task/me/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом для несуществующей задачи")
    @WithMockUser(roles = "AUTHOR")
    void updateTaskByTaskIdShouldReturnNotFoundExceptionForExecutor() throws Exception {
        when(taskService.updateTaskById(anyInt(), any(TaskDBOToUpdateTaskByTaskId.class), any(BindingResult.class))).thenThrow(new TaskNotFoundException("Задача не найдена"));

        String dBOJson = objectMapper.writeValueAsString(dboToUpdateTaskByTaskId);

        mockMvc.perform(MockMvcRequestBuilders.put("/task/me/{taskId}", taskId)
                        .content(dBOJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("Успешный тест поиска всех задач у автора")
    @WithMockUser(roles = "AUTHOR")
    void getMyAllTasksSuccess() throws Exception {
        List<TaskServiceResponseModel> expectedResponse = List.of(
                new TaskServiceResponseModel(),
                new TaskServiceResponseModel(),
                new TaskServiceResponseModel()
        );

        when(taskService.getAllTasksFromAuthor()).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/task/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 401-ым статусом для неавторизованного аккаунта")
    @WithAnonymousUser
    void getMyAllTasksShouldReturnUnAuthorizeException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/task/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом для пользователя с ролью ROLE_EXECUTOR")
    @WithMockUser(roles = "EXECUTOR")
    void getMyAllTasksForbiddenExceptionForExecutor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/task/me"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Успешный тест поиска всех задач у автора")
    @WithMockUser(roles = "AUTHOR")
    void getMyTaskByTaskIdSuccess() throws Exception {
        when(taskService.getTaskByTaskId(taskId)).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/task/me/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(expectedResponse.getTaskId()))
                .andExpect(jsonPath("$.title").value(expectedResponse.getTitle()))
                .andExpect(jsonPath("$.description").value(expectedResponse.getDescription()))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus()))
                .andExpect(jsonPath("$.priority").value(expectedResponse.getPriority()))
                .andExpect(jsonPath("$.author").value(expectedResponse.getAuthor()))
                .andExpect(jsonPath("$.executor").value(expectedResponse.getExecutor()))
                .andExpect(jsonPath("$.comment").value(expectedResponse.getComment()))
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом, если taskId <= 0 или null")
    @WithMockUser(roles = "AUTHOR")
    void getMyTaskByTaskIdShouldReturnBadRequestExceptionForWrongTaskId() throws Exception {
        taskId = -1;

        mockMvc.perform(MockMvcRequestBuilders.get("/task/me/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 401-ым статусом для неавторизованного аккаунта")
    @WithAnonymousUser
    void getMyTaskByTaskIdShouldReturnUnAuthorizeException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/task/me/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом для пользователя с ролью ROLE_EXECUTOR")
    @WithMockUser(roles = "EXECUTOR")
    void getMyTaskByTaskIdForbiddenExceptionForExecutor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/task/me/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом для несуществующей задачи")
    @WithMockUser(roles = "AUTHOR")
    void getMyTaskByTaskIdShouldReturnNotFoundExceptionForExecutor() throws Exception {
        taskId = 10;

        when(taskService.getTaskByTaskId(anyInt())).thenThrow(new TaskNotFoundException("Задача не найдена"));

        mockMvc.perform(MockMvcRequestBuilders.get("/task/me/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("Успешный тест удаления задачи по taskId")
    @WithMockUser(roles = "AUTHOR")
    void deleteMyTaskByTaskIdSuccess() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/task/me/{taskId}", taskId))
                    .andDo(print())
                    .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом, если taskId <= 0 или null")
    @WithMockUser(roles = "AUTHOR")
    void deleteMyTaskByTaskIdShouldReturnBadRequestExceptionForWrongTaskId() throws Exception {
        taskId = -1;

        mockMvc.perform(MockMvcRequestBuilders.delete("/task/me/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 401-ым статусом для неавторизованного аккаунта")
    @WithAnonymousUser
    void deleteMyTaskByTaskIdShouldReturnUnAuthorizeException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/task/me/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом для пользователя с ролью ROLE_EXECUTOR")
    @WithMockUser(roles = "EXECUTOR")
    void deleteMyTaskByTaskIdForbiddenExceptionForExecutor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/task/me/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом для несуществующей задачи")
    @WithMockUser(roles = "AUTHOR")
    void deleteMyTaskByTaskIdShouldReturnNotFoundExceptionForExecutor() throws Exception {
        taskId = 10;

        doThrow(new TaskNotFoundException("Задача не найдена"))
                .when(taskService)
                .deleteTaskByTaskId(taskId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/task/me/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешный тест обновления статуса у задания")
    @WithMockUser(roles = "AUTHOR")
    void updateStatusOfTaskByTaskIdForAuthorSuccess() throws Exception {
        when(taskService.updateStatusOfTaskByTaskIdForAuthor(eq(taskId), any(StatusDBO.class), any(BindingResult.class))).thenReturn(expectedResponse);

        String dBOJson = objectMapper.writeValueAsString(statusDBO);

        mockMvc.perform(MockMvcRequestBuilders.put("/task/author/{taskId}/status", taskId)
                .content(dBOJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(expectedResponse.getTaskId()))
                .andExpect(jsonPath("$.title").value(expectedResponse.getTitle()))
                .andExpect(jsonPath("$.description").value(expectedResponse.getDescription()))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus()))
                .andExpect(jsonPath("$.priority").value(expectedResponse.getPriority()))
                .andExpect(jsonPath("$.author").value(expectedResponse.getAuthor()))
                .andExpect(jsonPath("$.executor").value(expectedResponse.getExecutor()))
                .andExpect(jsonPath("$.comment").value(expectedResponse.getComment()))
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом")
    @WithMockUser(roles = "AUTHOR")
    void updateStatusOfTaskByTaskIdForAuthorShouldReturnBadRequestException() throws Exception {
        taskId = -1;

        mockMvc.perform(MockMvcRequestBuilders.put("/task/author/{taskId}/status", taskId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 401-ым статусом для неавторизованного аккаунта")
    @WithAnonymousUser
    void updateStatusOfTaskByTaskIdForAuthorShouldReturnUnAuthorizeException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/task/{taskId}/status", taskId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом")
    @WithMockUser(roles = "EXECUTOR")
    void updateStatusOfTaskByTaskIdForAuthorShouldReturnForbiddenException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/task/author/{taskId}/status", taskId))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом")
    @WithMockUser(roles = "AUTHOR")
    void updateStatusOfTaskByTaskIdForAuthorShouldReturnNotFoundExceptionForExecutor() throws Exception {
        taskId = 10;

        when(taskService.updateStatusOfTaskByTaskIdForAuthor(anyInt(), any(StatusDBO.class), any(BindingResult.class)))
                .thenThrow(new TaskNotFoundException("Задача не найдена"));

        String dBOJson = objectMapper.writeValueAsString(statusDBO);

        mockMvc.perform(MockMvcRequestBuilders.put("/task/author/{taskId}/status", taskId)
                        .content(dBOJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешный тест обновления исполнителя у задания")
    @WithMockUser(roles = "AUTHOR")
    void updateExecutorOfTaskByTaskIdSuccess() throws Exception {
        when(taskService.updateExecutorOfTaskByTaskId(eq(taskId), eq(executorId))).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/task/{taskId}/executor/{executorId}", taskId, executorId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(expectedResponse.getTaskId()))
                .andExpect(jsonPath("$.title").value(expectedResponse.getTitle()))
                .andExpect(jsonPath("$.description").value(expectedResponse.getDescription()))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus()))
                .andExpect(jsonPath("$.priority").value(expectedResponse.getPriority()))
                .andExpect(jsonPath("$.author").value(expectedResponse.getAuthor()))
                .andExpect(jsonPath("$.executor").value(expectedResponse.getExecutor()))
                .andExpect(jsonPath("$.comment").value(expectedResponse.getComment()))
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом")
    @WithMockUser(roles = "AUTHOR")
    void updateExecutorOfTaskByTaskIdShouldReturnBadRequestException() throws Exception {
        taskId = -1;

        mockMvc.perform(MockMvcRequestBuilders.put("/task/{taskId}/executor/{executorId}", taskId, executorId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 401-ым статусом для неавторизованного аккаунта")
    @WithAnonymousUser
    void updateExecutorOfTaskByTaskIdShouldReturnUnAuthorizeException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/task/{taskId}/executor/{executorId}", taskId, executorId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом")
    @WithMockUser(roles = "EXECUTOR")
    void updateExecutorOfTaskByTaskIdShouldReturnForbiddenException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/task/{taskId}/executor/{executorId}", taskId, executorId))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом")
    @WithMockUser(roles = "AUTHOR")
    void updateExecutorOfTaskByTaskIdShouldReturnNotFoundExceptionForExecutor() throws Exception {
        taskId = 10;

        when(taskService.updateExecutorOfTaskByTaskId(anyInt(), anyInt()))
                .thenThrow(new TaskNotFoundException("Задача не найдена"));

        mockMvc.perform(MockMvcRequestBuilders.put("/task/{taskId}/executor/{executorId}", taskId, executorId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешный тест обновления статуса у задания")
    @WithMockUser(roles = "EXECUTOR")
    void updateStatusOfTaskByTaskIdForExecutorSuccess() throws Exception {
        when(taskService.updateStatusOfTaskByTaskIdForExecutor(anyInt(), any(StatusDBO.class), any(BindingResult.class))).thenReturn(expectedResponse);

        String dBOJson = objectMapper.writeValueAsString(statusDBO);

        mockMvc.perform(MockMvcRequestBuilders.put("/task/executor/{taskId}/status", taskId)
                        .content(dBOJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(expectedResponse.getTaskId()))
                .andExpect(jsonPath("$.title").value(expectedResponse.getTitle()))
                .andExpect(jsonPath("$.description").value(expectedResponse.getDescription()))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus()))
                .andExpect(jsonPath("$.priority").value(expectedResponse.getPriority()))
                .andExpect(jsonPath("$.author").value(expectedResponse.getAuthor()))
                .andExpect(jsonPath("$.executor").value(expectedResponse.getExecutor()))
                .andExpect(jsonPath("$.comment").value(expectedResponse.getComment()))
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом")
    @WithMockUser(roles = "EXECUTOR")
    void updateStatusOfTaskByTaskIdForExecutorShouldReturnBadRequestException() throws Exception {
        taskId = -1;

        mockMvc.perform(MockMvcRequestBuilders.put("/task/executor/{taskId}/status", taskId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 401-ым статусом для неавторизованного аккаунта")
    @WithAnonymousUser
    void updateStatusOfTaskByTaskIdForExecutorShouldReturnUnAuthorizeException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/task/executor/{taskId}/status", taskId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом")
    @WithMockUser(roles = "AUTHOR")
    void updateStatusOfTaskByTaskIdForExecutorShouldReturnForbiddenException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/task/executor/{taskId}/status", taskId))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом")
    @WithMockUser(roles = "EXECUTOR")
    void updateStatusOfTaskByTaskIdForExecutorShouldReturnNotFoundExceptionForExecutor() throws Exception {
        taskId = 10;

        when(taskService.updateStatusOfTaskByTaskIdForExecutor(anyInt(), any(StatusDBO.class), any(BindingResult.class)))
                .thenThrow(new TaskNotFoundException("Задача не найдена"));

        String dBOJson = objectMapper.writeValueAsString(statusDBO);

        mockMvc.perform(MockMvcRequestBuilders.put("/task/executor/{taskId}/status", taskId)
                        .content(dBOJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешный тест поиска всех задач у аккаунта")
    @WithMockUser(roles = {"AUTHOR", "EXECUTOR"})
    void getTasksByAccountIdAndFiltersSuccess() throws Exception {
        when(taskService.getTasksByAccountIdAndFilters(anyInt(), anyString(), anyString())).thenReturn(List.of(expectedResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/task/{accountId}/search", executorId)
                        .param("status", status)
                        .param("priority", priority))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<TaskServiceResponseModel> actualResponse = taskService.getTasksByAccountIdAndFilters(executorId, status.toLowerCase(), priority.toLowerCase());

        Assertions.assertEquals(List.of(expectedResponse), actualResponse);

        verify(taskService).getTasksByAccountIdAndFilters(executorId, status.toLowerCase(), priority.toLowerCase());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 400-ым статусом")
    @WithMockUser(roles = {"AUTHOR", "EXECUTOR"})
    void getTasksByAccountIdAndFiltersShouldReturnBadRequestException() throws Exception {
        executorId = -1;

        mockMvc.perform(MockMvcRequestBuilders.get("/task/{accountId}/search", executorId)
                        .param("status", status)
                        .param("priority", priority))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 401-ым статусом для неавторизованного аккаунта")
    @WithAnonymousUser
    void getTasksByAccountIdAndFiltersShouldReturnUnAuthorizeException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/task/{accountId}/search", executorId)
                        .param("status", status)
                        .param("priority", priority))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 404-ым статусом")
    @WithMockUser(roles = {"AUTHOR", "EXECUTOR"})
    void getTasksByAccountIdAndFiltersShouldReturnNotFoundExceptionForExecutor() throws Exception {
        executorId = 10;

        when(taskService.getTasksByAccountIdAndFilters(anyInt(), anyString(), anyString()))
                .thenThrow(new TaskNotFoundException("Аккаунт не найден"));

        mockMvc.perform(MockMvcRequestBuilders.get("/task/{accountId}/search", executorId)
                        .param("status", status)
                        .param("priority", priority))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}