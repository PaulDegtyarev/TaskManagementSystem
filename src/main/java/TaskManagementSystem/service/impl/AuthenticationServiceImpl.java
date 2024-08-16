package TaskManagementSystem.service.impl;

import TaskManagementSystem.dataStore.AuthenticationDS;
import TaskManagementSystem.dto.dataStoreResponse.RegistrationDSResponseModel;
import TaskManagementSystem.dto.dbo.RegistrationDBO;
import TaskManagementSystem.presenter.AuthenticationPresenter;
import TaskManagementSystem.repository.AccountRepository;
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
    private AuthenticationDS authenticationDS;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationPresenter authenticationPresenter, AccountRepository accountRepository, AuthenticationDS authenticationDS) {
        this.authenticationPresenter = authenticationPresenter;
        this.accountRepository = accountRepository;
        this.authenticationDS = authenticationDS;
    }

    @Override
    public RegistrationDSResponseModel registration(RegistrationDBO dto, BindingResult bindingResult) {
        if (!isAnonymousUser()) {throw authenticationPresenter.prepareForbiddenView("Вы уже авторизованы");}

        if (accountRepository.existsByEmail(dto.getEmail())) {throw authenticationPresenter.prepareConflictView("Email занят");}

        checkData(dto, bindingResult);

        RegistrationDBO dtoWithHashedPassword = hashPassword(dto);

        RegistrationDSResponseModel registeredAccount = authenticationDS.registration(dtoWithHashedPassword);

        RegistrationDSResponseModel formatedResponse = formatResponse(registeredAccount);

        return authenticationPresenter.prepareSuccessView(formatedResponse);
    }

    private boolean isAnonymousUser() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal() == "anonymousUser";
    }

    private void checkData(RegistrationDBO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {throw authenticationPresenter.prepareBadRequestView("Неверно введенны данные");}

        if (dto.getRole().equalsIgnoreCase(RoleUtil.EXECUTOR_Ru)) {
            dto.setRole(RoleUtil.EXECUTOR_En);
        }

        else if (dto.getRole().equalsIgnoreCase(RoleUtil.AUTHOR_Ru)) {
            dto.setRole(RoleUtil.AUTHOR_En);
        }

        else {
            throw authenticationPresenter.prepareBadRequestView("Такой роли нет");
        }
    }

    private RegistrationDBO hashPassword(RegistrationDBO dto) {
        String notHashedPassword = dto.getPassword();

        dto.setPassword(encoder.encode(notHashedPassword));

        return dto;
    }

    private RegistrationDSResponseModel formatResponse(RegistrationDSResponseModel registeredAccount) {
        if (registeredAccount.getRole().equals(RoleUtil.EXECUTOR_En)) {
            registeredAccount.setRole("Исполнитель");
        }

        else if (registeredAccount.getRole().equals(RoleUtil.AUTHOR_En)) {
            registeredAccount.setRole("Автор");
        }

        return registeredAccount;
    }
}
