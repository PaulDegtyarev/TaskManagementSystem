package TaskManagementSystem.dataStore;

import TaskManagementSystem.dto.serviceResponse.RegistrationServiceResponseModel;
import TaskManagementSystem.dto.dbo.RegistrationDBO;

public interface AuthenticationDS {
    RegistrationServiceResponseModel registration(RegistrationDBO dto);
}
