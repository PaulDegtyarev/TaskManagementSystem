package TaskManagementSystem.dto.dbo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegistrationDBO {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email(regexp = "^[a-zA-Z0-9_.]+@[a-zA-Z0-9.-]+$")
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String role;

    public RegistrationDBO(String firstName, String lastName, String email, String password, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public @NotBlank String getFirstName() {
        return firstName;
    }

    public @NotBlank String getLastName() {
        return lastName;
    }

    public @NotBlank @Email(regexp = "^[a-zA-Z0-9_.]+@[a-zA-Z0-9.-]+$") String getEmail() {
        return email;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setRole(@NotBlank String role) {
        this.role = role;
    }

    public @NotBlank String getRole() {
        return role;
    }
}
