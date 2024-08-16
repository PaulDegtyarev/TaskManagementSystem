package TaskManagementSystem.exception.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TaskBadRequestException extends ResponseStatusException {
    public TaskBadRequestException(String message) {super(HttpStatus.BAD_REQUEST, message);}
}
