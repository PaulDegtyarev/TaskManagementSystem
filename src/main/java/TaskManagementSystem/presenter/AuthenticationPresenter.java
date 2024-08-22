package TaskManagementSystem.presenter;

import TaskManagementSystem.dto.serviceResponse.RegistrationServiceResponseModel;
import TaskManagementSystem.exception.authentication.AuthenticationBadRequestException;
import TaskManagementSystem.exception.authentication.AuthenticationConflictException;
import TaskManagementSystem.exception.authentication.AuthenticationForbiddenException;
import TaskManagementSystem.exception.authentication.AuthenticationNotFoundException;

public interface AuthenticationPresenter {
    AuthenticationNotFoundException prepareNotFoundView(String message);

    AuthenticationBadRequestException prepareBadRequestView(String message);

    AuthenticationConflictException prepareConflictView(String message);

    AuthenticationForbiddenException prepareForbiddenView(String message);

    RegistrationServiceResponseModel prepareSuccessView(RegistrationServiceResponseModel response);
}
