package TaskManagementSystem.presenter;

import TaskManagementSystem.dto.dataStoreResponse.RegistrationDSResponseModel;
import TaskManagementSystem.exception.authentication.AuthenticationBadRequestException;
import TaskManagementSystem.exception.authentication.AuthenticationConflictException;
import TaskManagementSystem.exception.authentication.AuthenticationForbiddenException;
import TaskManagementSystem.exception.authentication.AuthenticationNotFoundException;

public interface AuthenticationPresenter {
    AuthenticationNotFoundException prepareNotFoundView(String message);

    AuthenticationBadRequestException prepareBadRequestView(String message);

    AuthenticationConflictException prepareConflictView(String message);

    AuthenticationForbiddenException prepareForbiddenView(String message);

    RegistrationDSResponseModel prepareSuccessView(RegistrationDSResponseModel response);
}
