package TaskManagementSystem.dto.dbo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
@Schema(description = "Сущность задачи для обновления")
public class TaskDBOToUpdateTaskByTaskId {
    @NotBlank
    @Schema(description = "Заголовок задачи")
    private String title;

    @NotBlank
    @Schema(description = "Описание задачи")
    private String description;

    @NotBlank
    @Schema(description = "Приоритет задачи")
    private String priority;

    @NotBlank
    @Schema(description = "Статус задачи")
    private String status;

    @NotNull
    @Min(1)
    @Schema(description = "Id автора")
    private Integer authorId;

    @NotNull
    @Min(1)
    @Schema(description = "Id исполнителя")
    private Integer executorId;

    @Schema(description = "Комментарий к задаче")
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
