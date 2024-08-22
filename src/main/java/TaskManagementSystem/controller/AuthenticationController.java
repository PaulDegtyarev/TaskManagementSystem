package TaskManagementSystem.controller;

import TaskManagementSystem.dto.serviceResponse.RegistrationServiceResponseModel;
import TaskManagementSystem.dto.dbo.RegistrationDBO;
import TaskManagementSystem.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signin")
    @Async
    public String signin(@RequestParam("username") String username, @RequestParam("password") String password) {
        return "signin";
    }

    @PostMapping("/registration")
    @Async
    @Transactional
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Позволяет зарегистрировать нового пользователя"
    )
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<RegistrationServiceResponseModel> registration(
            @RequestBody @Valid @Parameter(description = "Request Body запроса с информацией о новом пользователе", required = true) RegistrationDBO dto,
            BindingResult bindingResult) {
        return new ResponseEntity<>(authenticationService.registration(dto, bindingResult), HttpStatus.CREATED);
    }

    @GetMapping("/success")
    @Async
    public ResponseEntity<String> successfulAuthentication() {
            return new ResponseEntity<>("You are successful authenticate", HttpStatus.OK);
    }
}
