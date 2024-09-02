package TaskManagementSystem.util;

import TaskManagementSystem.dto.dbo.RegistrationDBO;
import TaskManagementSystem.dto.serviceResponse.RegistrationServiceResponseModel;
import TaskManagementSystem.entity.AccountEntity;

public interface RegistrationRoleHelper {
    void checkRoleInData(RegistrationDBO dto);

    RegistrationServiceResponseModel formatResponse(AccountEntity registeredAccount);
}
