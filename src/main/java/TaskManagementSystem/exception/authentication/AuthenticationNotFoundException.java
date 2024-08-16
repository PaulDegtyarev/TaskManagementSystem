package TaskManagementSystem.exception.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthenticationNotFoundException extends ResponseStatusException {
    public AuthenticationNotFoundException(String message) {super(HttpStatus.NOT_FOUND, message);}
}
