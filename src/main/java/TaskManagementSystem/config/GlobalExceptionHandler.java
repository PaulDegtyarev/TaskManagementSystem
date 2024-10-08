package TaskManagementSystem.config;

import TaskManagementSystem.exception.authentication.AuthenticationBadRequestException;
import TaskManagementSystem.exception.authentication.AuthenticationConflictException;
import TaskManagementSystem.exception.authentication.AuthenticationForbiddenException;
import TaskManagementSystem.exception.authentication.AuthenticationNotFoundException;
import TaskManagementSystem.exception.task.TaskBadRequestException;
import TaskManagementSystem.exception.task.TaskForbiddenException;
import TaskManagementSystem.exception.task.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthenticationNotFoundException.class)
    public ResponseEntity<String> handlerAuthenticationNotFoundException(AuthenticationNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(AuthenticationBadRequestException.class)
    public ResponseEntity<String> handlerAuthenticationBadRequestException(AuthenticationBadRequestException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(AuthenticationForbiddenException.class)
    public ResponseEntity<String> handlerAuthenticationForbiddenException(AuthenticationForbiddenException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(exception.getMessage());
    }

    @ExceptionHandler(AuthenticationConflictException.class)
    public ResponseEntity<String> handlerAuthenticationConflictException(AuthenticationConflictException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    @ExceptionHandler(TaskBadRequestException.class)
    public ResponseEntity<String> handlerTaskBadRequestException(TaskBadRequestException exception) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(exception.getMessage());
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handlerTaskNotFoundException(TaskNotFoundException exception) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(exception.getMessage());
    }

    @ExceptionHandler(TaskForbiddenException.class)
    public ResponseEntity<String> handlerTaskNTaskForbiddenException(TaskForbiddenException exception) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(exception.getMessage());
    }
}
