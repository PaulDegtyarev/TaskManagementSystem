package TaskManagementSystem.dto.dbo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Сущность задачи для создания")
public class GeneralTaskDBO {
    @NotBlank
    @Schema(description = "Заголовок задачи")
    private String title;

    @NotBlank
    @Schema(description = "Описание задачи")
    private String description;

    @NotBlank
    @Schema(description = "Приоритет задачи")
    private String priority;

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

    public GeneralTaskDBO(String title, String description, String priority, Integer authorId, Integer executorId, String comment) {
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
}
