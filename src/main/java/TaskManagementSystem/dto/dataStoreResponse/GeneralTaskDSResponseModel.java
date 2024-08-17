package TaskManagementSystem.dto.dataStoreResponse;

import java.util.Objects;

public class GeneralTaskDSResponseModel {
    private Integer taskId;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String author;
    private String executor;
    private String comment;

    public GeneralTaskDSResponseModel(){}

    public GeneralTaskDSResponseModel(Integer taskId, String title, String description, String status, String priority, String author, String executor, String comment) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.author = author;
        this.executor = executor;
        this.comment = comment;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getAuthor() {
        return author;
    }

    public String getExecutor() {
        return executor;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneralTaskDSResponseModel that = (GeneralTaskDSResponseModel) o;
        return Objects.equals(taskId, that.taskId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(status, that.status) &&
                Objects.equals(priority, that.priority) &&
                Objects.equals(author, that.author) &&
                Objects.equals(executor, that.executor) &&
                Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, title, status, priority, author, executor, comment);
    }

    public String getDescription() {
        return description;
    }
}
