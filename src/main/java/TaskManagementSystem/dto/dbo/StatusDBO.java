package TaskManagementSystem.dto.dbo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Сущность статуса")
public class StatusDBO {
    @NotBlank
    @Schema(description = "Новый статус задачи")
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