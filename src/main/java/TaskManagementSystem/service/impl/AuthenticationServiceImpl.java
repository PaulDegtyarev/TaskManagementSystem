package TaskManagementSystem.service.impl;

import TaskManagementSystem.dto.serviceResponse.RegistrationServiceResponseModel;
import TaskManagementSystem.dto.dbo.RegistrationDBO;
import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.entity.RoleEntity;
import TaskManagementSystem.exception.authentication.AuthenticationBadRequestException;
import TaskManagementSystem.exception.authentication.AuthenticationConflictException;
import TaskManagementSystem.exception.authentication.AuthenticationForbiddenException;
import TaskManagementSystem.presenter.AuthenticationPresenter;
import TaskManagementSystem.repository.AccountRepository;
import TaskManagementSystem.repository.RoleRepository;
import TaskManagementSystem.service.AuthenticationService;
import TaskManagementSystem.service.HashService;
import TaskManagementSystem.service.SecurityContextService;
import TaskManagementSystem.util.RegistrationRoleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private SecurityContextService securityContextService;
    private AuthenticationPresenter authenticationPresenter;
    private AccountRepository accountRepository;
    private RegistrationRoleHelper registrationRoleHelper;
    private HashService hashService;
    private RoleRepository roleRepository;

    @Autowired
    public AuthenticationServiceImpl(SecurityContextService securityContextService, AuthenticationPresenter authenticationPresenter, AccountRepository accountRepository, RegistrationRoleHelper registrationRoleHelper, HashService hashService, RoleRepository roleRepository) {
        this.securityContextService = securityContextService;
        this.authenticationPresenter = authenticationPresenter;
        this.accountRepository = accountRepository;
        this.registrationRoleHelper = registrationRoleHelper;
        this.hashService = hashService;
        this.roleRepository = roleRepository;
    }

    @Override
    public RegistrationServiceResponseModel registration(RegistrationDBO dto, BindingResult bindingResult) {
        try {
            if (!securityContextService.isAnonymousUser()) {throw new AuthenticationForbiddenException("Вы уже авторизованы");}

            if (accountRepository.existsByEmail(dto.getEmail())) {throw new AuthenticationConflictException("Email занят");}

            if (bindingResult.hasErrors()) {throw new AuthenticationBadRequestException("Неверно введенны данные");}

            registrationRoleHelper.checkRoleInData(dto);

        } catch (AuthenticationBadRequestException authenticationBadRequestException) {
            throw authenticationPresenter.prepareBadRequestView(authenticationBadRequestException.getMessage());
        } catch (AuthenticationForbiddenException authenticationForbiddenException) {
            throw authenticationPresenter.prepareForbiddenView(authenticationForbiddenException.getMessage());
        } catch (AuthenticationConflictException authenticationConflictException) {
            throw authenticationPresenter.prepareConflictView(authenticationConflictException.getMessage());
        }

        RegistrationDBO dboWithHashedPassword = hashService.hashPassword(dto);

        RoleEntity roleEntity = roleRepository.findByRole(dboWithHashedPassword.getRole());

        AccountEntity newAccount = new AccountEntity(
                dboWithHashedPassword.getEmail(),
                dboWithHashedPassword.getPassword(),
                dboWithHashedPassword.getFirstName(),
                dboWithHashedPassword.getLastName(),
                roleEntity.getRoleId()
        );
        newAccount.setRoleEntity(roleEntity);

        accountRepository.save(newAccount);

        RegistrationServiceResponseModel formatedResponse = registrationRoleHelper.formatResponse(newAccount);

        return authenticationPresenter.prepareSuccessView(formatedResponse);
    }
}