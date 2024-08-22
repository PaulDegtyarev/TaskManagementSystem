package TaskManagementSystem.service;

import TaskManagementSystem.dto.serviceResponse.RegistrationServiceResponseModel;
import TaskManagementSystem.dto.dbo.RegistrationDBO;
import org.springframework.validation.BindingResult;

public interface AuthenticationService {
    RegistrationServiceResponseModel registration(RegistrationDBO dto, BindingResult bindingResult);
}
