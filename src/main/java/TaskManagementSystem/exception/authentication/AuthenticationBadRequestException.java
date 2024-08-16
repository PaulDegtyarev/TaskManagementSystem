package TaskManagementSystem.exception.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthenticationBadRequestException extends ResponseStatusException {
    public AuthenticationBadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
