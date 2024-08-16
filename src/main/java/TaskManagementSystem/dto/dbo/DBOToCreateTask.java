package TaskManagementSystem.dto.dbo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DBOToCreateTask {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String status;

    @NotBlank
    private String priority;

    @NotNull
    @Min(1)
    private Integer authorId;

    @NotNull
    @Min(1)
    private Integer executorId;

    public DBOToCreateTask(String title, String description, String status, String priority, Integer authorId, Integer executorId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.authorId = authorId;
        this.executorId = executorId;
    }

    public @NotBlank String getTitle() {
        return title;
    }

    public @NotBlank String getDescription() {
        return description;
    }

    public @NotBlank String getStatus() {
        return status;
    }

    public @NotBlank String getPriority() {
        return priority;
    }

    public @NotNull @Min(1) Integer getAuthorId() {
        return authorId;
    }

    public @NotNull @Min(1) Integer getExecutorId() {
        return executorId;
    }
}
