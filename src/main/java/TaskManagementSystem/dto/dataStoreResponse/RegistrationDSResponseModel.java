package TaskManagementSystem.dto.dataStoreResponse;

public class RegistrationDSResponseModel {
    private Integer accountId;
    private String email;
    private String firstname;
    private String lastname;
    private String role;

    public RegistrationDSResponseModel(Integer accountId, String email, String firstname, String lastname, String role) {
        this.accountId = accountId;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
