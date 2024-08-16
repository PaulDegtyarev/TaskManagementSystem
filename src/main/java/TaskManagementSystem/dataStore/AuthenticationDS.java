package TaskManagementSystem.dataStore;

import TaskManagementSystem.dto.dataStoreResponse.RegistrationDSResponseModel;
import TaskManagementSystem.dto.dbo.RegistrationDBO;

public interface AuthenticationDS {
    RegistrationDSResponseModel registration(RegistrationDBO dto);
}
