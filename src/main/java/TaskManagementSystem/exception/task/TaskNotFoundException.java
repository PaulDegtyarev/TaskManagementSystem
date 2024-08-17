package TaskManagementSystem.exception.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TaskNotFoundException extends ResponseStatusException {
    public TaskNotFoundException(String message) {super(HttpStatus.NOT_FOUND, message);}
}
