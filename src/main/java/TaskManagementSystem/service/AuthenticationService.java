package TaskManagementSystem.service;

import TaskManagementSystem.dto.dataStoreResponse.RegistrationDSResponseModel;
import TaskManagementSystem.dto.dbo.RegistrationDBO;
import org.springframework.validation.BindingResult;

public interface AuthenticationService {
    RegistrationDSResponseModel registration(RegistrationDBO dto, BindingResult bindingResult);
}
