package TaskManagementSystem.presenter.impl;

import TaskManagementSystem.exception.AuthenticationNotFoundException;
import TaskManagementSystem.presenter.AuthenticationPresenter;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFormatter implements AuthenticationPresenter {
    @Override
    public AuthenticationNotFoundException prepareNotFoundView(String message) {
        throw new AuthenticationNotFoundException(message);}
}
