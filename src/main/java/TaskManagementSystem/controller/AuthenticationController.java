package TaskManagementSystem.controller;

import TaskManagementSystem.dto.dataStoreResponse.RegistrationDSResponseModel;
import TaskManagementSystem.dto.dbo.RegistrationDBO;
import TaskManagementSystem.service.AuthenticationService;
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

    @GetMapping("/signin")
    @Async
    public String signin() {
        return "signin";
    }

    @PostMapping("/registration")
    @Async
    @Transactional
    public ResponseEntity<RegistrationDSResponseModel> registration(
            @RequestBody @Valid RegistrationDBO dto,
            BindingResult bindingResult) {
        return new ResponseEntity<>(authenticationService.registration(dto, bindingResult), HttpStatus.CREATED);
    }

    @GetMapping("/success")
    @Async
    public ResponseEntity<String> successfulAuthentication() {
            return new ResponseEntity<>("You are successful authenticate", HttpStatus.OK);
    }
}
