package TaskManagementSystem.presenter;

import TaskManagementSystem.exception.AuthenticationNotFoundException;

public interface AuthenticationPresenter {
    AuthenticationNotFoundException prepareNotFoundView(String message);
}
