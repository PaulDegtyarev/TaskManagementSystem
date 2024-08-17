package TaskManagementSystem.controller;

import TaskManagementSystem.TaskManagementSystemApplication;
import TaskManagementSystem.config.SecurityConfig;
import TaskManagementSystem.dto.dataStoreResponse.GeneralTaskDSResponseModel;
import TaskManagementSystem.dto.dbo.TaskDBO;
import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.entity.RoleEntity;
import TaskManagementSystem.exception.task.TaskForbiddenException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
    private TaskDBO dto;
    private GeneralTaskDSResponseModel expectedResponse;
    private Integer taskId;

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

        dto = new TaskDBO(
                "Подписать бумажку",
                "Купить бумагу в магазине, подписать ее ручкой",
                "Высокий",
                3,
                4,
                "Перед сдачей задачи напишите мне или позвоните"
        );

        executorEntity = new AccountEntity(
                "executor@executor.ru",
                "executor",
                "executorFirstName",
                "executorLastName",
                2
        );
        executorEntity.setRoleEntity(new RoleEntity("ROLE_EXECUTOR"));

        taskId = 2;

        expectedResponse = new GeneralTaskDSResponseModel(
                taskId,
                dto.getTitle(),
                dto.getDescription(),
                "в ожидании",
                dto.getPriority().toLowerCase(),
                "author@author.ru",
                executorEntity.getEmail(),
                dto.getComment()
        );
    }

    @Test
    @DisplayName("Успешный тест создания задачи")
    @WithMockUser(roles = "AUTHOR")
    void createTaskSuccess() throws Exception {
        when(taskService.createTask(any(TaskDBO.class), any(BindingResult.class))).thenReturn(expectedResponse);

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
    void updateTaskByIdSuccess() throws Exception {
        when(taskService.updateTaskById(eq(taskId), any(TaskDBO.class), any(BindingResult.class))).thenReturn(expectedResponse);

        String jsonDTO = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put("/task/{taskId}", taskId)
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
    @DisplayName("Тест выбрасывающий исключение с 401-ым статусом для неавторизованного аккаунта")
    @WithAnonymousUser
    void updateTaskByIdShouldReturnUnAuthorizeException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/task/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение с 403-им статусом для пользователя с ролью ROLE_EXECUTOR")
    @WithMockUser(roles = "EXECUTOR")
    void updateTaskByIdShouldReturnForbiddenExceptionForExecutor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/task/{taskId}", taskId))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
