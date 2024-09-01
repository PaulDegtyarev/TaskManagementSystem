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
import TaskManagementSystem.util.RoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private AuthenticationPresenter authenticationPresenter;
    private AccountRepository accountRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private RoleRepository roleRepository;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationPresenter authenticationPresenter, AccountRepository accountRepository, RoleRepository roleRepository) {
        this.authenticationPresenter = authenticationPresenter;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public RegistrationServiceResponseModel registration(RegistrationDBO dto, BindingResult bindingResult) {
        try {
            if (!isAnonymousUser()) {throw new AuthenticationForbiddenException("Вы уже авторизованы");}

            if (accountRepository.existsByEmail(dto.getEmail())) {throw new AuthenticationConflictException("Email занят");}

            if (bindingResult.hasErrors()) {throw new AuthenticationBadRequestException("Неверно введенны данные");}

            checkRoleInData(dto);

        } catch (AuthenticationBadRequestException authenticationBadRequestException) {
            throw authenticationPresenter.prepareBadRequestView(authenticationBadRequestException.getMessage());
        } catch (AuthenticationForbiddenException authenticationForbiddenException) {
            throw authenticationPresenter.prepareForbiddenView(authenticationForbiddenException.getMessage());
        } catch (AuthenticationConflictException authenticationConflictException) {
            throw authenticationPresenter.prepareConflictView(authenticationConflictException.getMessage());
        }

        hashPassword(dto);

        RoleEntity roleEntity = roleRepository.findByRole(dto.getRole());

        AccountEntity newAccount = new AccountEntity(
                dto.getEmail(),
                dto.getPassword(),
                dto.getFirstName(),
                dto.getLastName(),
                roleEntity.getRoleId()
        );
        newAccount.setRoleEntity(roleEntity);

        accountRepository.save(newAccount);

        RegistrationServiceResponseModel formatedResponse = formatResponse(newAccount);

        return authenticationPresenter.prepareSuccessView(formatedResponse);
    }

    private boolean isAnonymousUser() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal() == "anonymousUser";
    }

    private void checkRoleInData(RegistrationDBO dto) {
        if (dto.getRole().equalsIgnoreCase(RoleUtil.EXECUTOR_Ru)) {dto.setRole(RoleUtil.EXECUTOR_En);}

        else if (dto.getRole().equalsIgnoreCase(RoleUtil.AUTHOR_Ru)) {dto.setRole(RoleUtil.AUTHOR_En);}

        else {throw new AuthenticationBadRequestException("Такой роли нет");}
    }

    private void hashPassword(RegistrationDBO dto) {
        String notHashedPassword = dto.getPassword();

        String hashedPassword = encoder.encode(notHashedPassword);

        dto.setPassword(hashedPassword);
    }

    private RegistrationServiceResponseModel formatResponse(AccountEntity registeredAccount) {
        String roleToAnswer = "";

        if (registeredAccount
                .getRoleEntity()
                .getRole()
                .equals(RoleUtil.EXECUTOR_En)) {roleToAnswer = "Исполнитель";}

        else if (registeredAccount
                .getRoleEntity()
                .getRole()
                .equals(RoleUtil.AUTHOR_En)) {roleToAnswer = "Автор";}

        return new RegistrationServiceResponseModel(
                registeredAccount.getAccountId(),
                registeredAccount.getEmail(),
                registeredAccount.getFirstname(),
                registeredAccount.getLastname(),
                roleToAnswer
        );
    }
}