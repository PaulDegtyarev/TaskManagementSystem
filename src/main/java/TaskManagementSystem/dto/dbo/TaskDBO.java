package TaskManagementSystem.dto.dbo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskDBO {
    @NotBlank
    private String title;

    @NotBlank

    private String description;

    private String status;

    @NotBlank
    private String priority;

    @NotNull
    @Min(1)
    private Integer authorId;

    @NotNull
    @Min(1)
    private Integer executorId;

    @NotBlank
    private String comment;

    public TaskDBO(String title, String description, String priority, Integer authorId, Integer executorId, String comment) {
        this.title = title;
        this.description = description;
        this.priority = priority;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
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

    public @NotBlank String getComment() {
        return comment;
    }
}
