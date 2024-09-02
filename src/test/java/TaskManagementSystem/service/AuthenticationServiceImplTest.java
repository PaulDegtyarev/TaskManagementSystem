package TaskManagementSystem.service;

import TaskManagementSystem.config.MyUserDetails;
import TaskManagementSystem.dto.dbo.RegistrationDBO;
import TaskManagementSystem.dto.serviceResponse.RegistrationServiceResponseModel;
import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.entity.RoleEntity;
import TaskManagementSystem.exception.authentication.AuthenticationBadRequestException;
import TaskManagementSystem.exception.authentication.AuthenticationConflictException;
import TaskManagementSystem.exception.authentication.AuthenticationForbiddenException;
import TaskManagementSystem.presenter.impl.AuthenticationFormatter;
import TaskManagementSystem.repository.AccountRepository;
import TaskManagementSystem.repository.RoleRepository;
import TaskManagementSystem.service.impl.AuthenticationServiceImpl;
import TaskManagementSystem.service.impl.HashServiceImpl;
import TaskManagementSystem.service.impl.SecurityContextServiceImpl;
import TaskManagementSystem.util.RoleUtil;
import TaskManagementSystem.util.impl.RegistrationRoleHelperImpl;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AuthenticationServiceImplTest {
    @Mock
    private SecurityContextServiceImpl securityContextService;

    @Mock
    private AuthenticationFormatter authenticationPresenter;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RegistrationRoleHelperImpl registrationRoleHelper;

    @Mock
    private HashServiceImpl hashService;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private BCryptPasswordEncoder encoder;
    private BindingResult bindingResult;
    private SecurityContext securityContext;
    private Authentication authentication;
    private MyUserDetails myUserDetails;
    private RegistrationDBO registrationDBO;
    private RegistrationDBO dboWithHashedPassword;
    private RoleEntity roleEntity;
    private Integer roleId;
    private AccountEntity accountEntity;
    private RegistrationServiceResponseModel expectedResponse;


    @BeforeEach
    void setUp() {
        encoder = mock(BCryptPasswordEncoder.class);
        bindingResult = mock(BindingResult.class);
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        authentication = mock(Authentication.class);
        myUserDetails = mock(MyUserDetails.class);

        registrationDBO = new RegistrationDBO(
                "testFirstName",
                "testLastName",
                "testEmail@gmail.com",
                "testPassword",
                "Исполнитель"
        );

        dboWithHashedPassword = new RegistrationDBO(
                registrationDBO.getFirstName(),
                registrationDBO.getLastName(),
                registrationDBO.getEmail(),
                "7418765657165275215721fhsafkjasfksfj",
                registrationDBO.getRole()
        );

        roleEntity = new RoleEntity(RoleUtil.EXECUTOR_En);

        roleId = 1;

        accountEntity = new AccountEntity(
                registrationDBO.getEmail(),
                registrationDBO.getPassword(),
                registrationDBO.getFirstName(),
                registrationDBO.getLastName(),
                roleId
        );

        expectedResponse = new RegistrationServiceResponseModel(
                1,
                registrationDBO.getEmail(),
                registrationDBO.getFirstName(),
                registrationDBO.getLastName(),
                registrationDBO.getRole()
        );
    }

    @Test
    @DisplayName("Успешный тест регистрации пользователя")
    void registrationSuccess() {
        when(!securityContextService.isAnonymousUser()).thenReturn(true);
        when(accountRepository.existsByEmail(anyString())).thenReturn(false);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(registrationRoleHelper.formatResponse(any(AccountEntity.class))).thenReturn(expectedResponse);
        when(hashService.hashPassword(registrationDBO)).thenReturn(dboWithHashedPassword);
        when(roleRepository.findByRole(dboWithHashedPassword.getRole())).thenReturn(roleEntity);
        when(authenticationPresenter.prepareSuccessView(expectedResponse)).thenReturn(expectedResponse);

        RegistrationServiceResponseModel actualResponse = authenticationService.registration(registrationDBO, bindingResult);

        Assertions.assertEquals(expectedResponse, actualResponse);
        verify(registrationRoleHelper).checkRoleInData(registrationDBO);
        verify(authenticationPresenter).prepareSuccessView(expectedResponse);
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение для авторизованного пользователя")
    void registrationShouldReturnForbiddenException() {
        when(!securityContextService.isAnonymousUser()).thenReturn(false);

        AuthenticationForbiddenException forbiddenException = new AuthenticationForbiddenException("Вы уже авторизованы");

        when(authenticationPresenter.prepareForbiddenView(forbiddenException.getMessage())).thenThrow(forbiddenException);

        Assertions.assertThrows(
                AuthenticationForbiddenException.class,
                () -> authenticationService.registration(registrationDBO, bindingResult)
        );

        verify(authenticationPresenter).prepareForbiddenView(forbiddenException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение для занятого email")
    void registrationShouldReturnConflictException() {
        when(!securityContextService.isAnonymousUser()).thenReturn(true);
        when(accountRepository.existsByEmail(registrationDBO.getEmail())).thenReturn(true);

        AuthenticationConflictException conflictException = new AuthenticationConflictException("Email занят");

        when(authenticationPresenter.prepareConflictView(conflictException.getMessage())).thenThrow(conflictException);

        Assertions.assertThrows(
                AuthenticationConflictException.class,
                () -> authenticationService.registration(registrationDBO, bindingResult)
        );

        verify(authenticationPresenter).prepareConflictView(conflictException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение при неверно введенных данных")
    void registrationShouldReturnBadRequestExceptionForWrongData() {
        when(!securityContextService.isAnonymousUser()).thenReturn(true);
        when(accountRepository.existsByEmail(registrationDBO.getEmail())).thenReturn(false);
        when(bindingResult.hasErrors()).thenReturn(true);

        AuthenticationBadRequestException badRequestException = new AuthenticationBadRequestException("Неверно введенны данные");

        when(authenticationPresenter.prepareBadRequestView(badRequestException.getMessage())).thenThrow(badRequestException);

        Assertions.assertThrows(
                AuthenticationBadRequestException.class,
                () -> authenticationService.registration(registrationDBO, bindingResult)
        );

        verify(authenticationPresenter).prepareBadRequestView(badRequestException.getMessage());
    }

    @Test
    @DisplayName("Тест выбрасывающий исключение при неверно введенной роли")
    void registrationShouldReturnBadRequestExceptionForWrongRole() {
        registrationDBO.setRole("НеКорректнаяРоль");
        when(!securityContextService.isAnonymousUser()).thenReturn(true);
        when(accountRepository.existsByEmail(registrationDBO.getEmail())).thenReturn(false);
        when(bindingResult.hasErrors()).thenReturn(false);

        AuthenticationBadRequestException badRequestException = new AuthenticationBadRequestException("Такой роли нет");

        doThrow(badRequestException).when(registrationRoleHelper).checkRoleInData(registrationDBO);

        when(authenticationPresenter.prepareBadRequestView(badRequestException.getMessage())).thenThrow(badRequestException);

        Assertions.assertThrows(
                AuthenticationBadRequestException.class,
                () -> authenticationService.registration(registrationDBO, bindingResult)
        );

        verify(registrationRoleHelper).checkRoleInData(registrationDBO);
        verify(authenticationPresenter).prepareBadRequestView(badRequestException.getMessage());
    }
}