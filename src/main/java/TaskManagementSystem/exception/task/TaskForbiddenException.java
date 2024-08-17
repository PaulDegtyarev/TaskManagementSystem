package TaskManagementSystem.exception.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TaskForbiddenException extends ResponseStatusException {
    public TaskForbiddenException(String message) {super(HttpStatus.FORBIDDEN, message);}
}
