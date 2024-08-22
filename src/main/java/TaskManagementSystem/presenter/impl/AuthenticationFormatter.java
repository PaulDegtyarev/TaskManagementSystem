package TaskManagementSystem.presenter.impl;

import TaskManagementSystem.dto.serviceResponse.RegistrationServiceResponseModel;
import TaskManagementSystem.exception.authentication.AuthenticationBadRequestException;
import TaskManagementSystem.exception.authentication.AuthenticationConflictException;
import TaskManagementSystem.exception.authentication.AuthenticationForbiddenException;
import TaskManagementSystem.exception.authentication.AuthenticationNotFoundException;
import TaskManagementSystem.presenter.AuthenticationPresenter;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFormatter implements AuthenticationPresenter {
    @Override
    public AuthenticationNotFoundException prepareNotFoundView(String message) {
        throw new AuthenticationNotFoundException(message);
    }

    @Override
    public AuthenticationBadRequestException prepareBadRequestView(String message) {
        throw new AuthenticationBadRequestException(message);
    }

    @Override
    public AuthenticationConflictException prepareConflictView(String message) {
        throw new AuthenticationConflictException(message);
    }

    @Override
    public AuthenticationForbiddenException prepareForbiddenView(String message) {
        throw new AuthenticationForbiddenException(message);
    }

    @Override
    public RegistrationServiceResponseModel prepareSuccessView(RegistrationServiceResponseModel response) {return response;}
}
