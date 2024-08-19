package TaskManagementSystem.dto.dbo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskDBOToUpdateTaskByTaskId {
    @NotBlank
    private String title;

    @NotBlank

    private String description;

    @NotBlank
    private String priority;

    @NotBlank
    private String status;

    @NotNull
    @Min(1)
    private Integer authorId;

    @NotNull
    @Min(1)
    private Integer executorId;

    private String comment;

    public TaskDBOToUpdateTaskByTaskId(String title, String description, String priority, String status, Integer authorId, Integer executorId, String comment) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.authorId = authorId;
        this.executorId = executorId;
        this.comment = comment;
    }

    public @NotBlank String getTitle() {
        return title;
    }

    public @NotBlank String getDescription() {
        return description;
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

    public String getComment() {
        return comment;
    }

    public @NotBlank String getStatus() {
        return status;
    }
}
