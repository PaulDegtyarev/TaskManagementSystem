package TaskManagementSystem.exception.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthenticationForbiddenException extends ResponseStatusException {
    public AuthenticationForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
