package TaskManagementSystem.util.impl;

import TaskManagementSystem.dto.dbo.RegistrationDBO;
import TaskManagementSystem.dto.serviceResponse.RegistrationServiceResponseModel;
import TaskManagementSystem.entity.AccountEntity;
import TaskManagementSystem.exception.authentication.AuthenticationBadRequestException;
import TaskManagementSystem.util.RegistrationRoleHelper;
import TaskManagementSystem.util.RoleUtil;
import org.springframework.stereotype.Component;

@Component
public class RegistrationRoleHelperImpl implements RegistrationRoleHelper {
    @Override
    public void checkRoleInData(RegistrationDBO dto) {
        if (dto.getRole().equalsIgnoreCase(RoleUtil.EXECUTOR_Ru)) {dto.setRole(RoleUtil.EXECUTOR_En);}

        else if (dto.getRole().equalsIgnoreCase(RoleUtil.AUTHOR_Ru)) {dto.setRole(RoleUtil.AUTHOR_En);}

        else {throw new AuthenticationBadRequestException("Такой роли нет");}
    }

    @Override
    public RegistrationServiceResponseModel formatResponse(AccountEntity registeredAccount) {
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
