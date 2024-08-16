package TaskManagementSystem.exception.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthenticationConflictException extends ResponseStatusException {
    public AuthenticationConflictException(String message) {super(HttpStatus.CONFLICT, message);}
}
