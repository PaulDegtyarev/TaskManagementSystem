package TaskManagementSystem.dto.dbo;

import jakarta.validation.constraints.NotBlank;

public class StatusDBO {
    @NotBlank
    private String status;

    public StatusDBO(){}

    public StatusDBO(String status) {
        this.status = status;
    }

    public void setStatus(@NotBlank String status) {
        this.status = status;
    }

    public @NotBlank String getStatus() {
        return status;
    }
}