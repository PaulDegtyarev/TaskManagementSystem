package TaskManagementSystem.dto.dataStoreResponse;

public class GeneralTaskDSResponseModel {
    private Integer taskId;
    private String title;
    private String status;
    private String priority;
    private String author;
    private String executor;

    public GeneralTaskDSResponseModel(Integer taskId, String title, String status, String priority, String author, String executor) {
        this.taskId = taskId;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.author = author;
        this.executor = executor;
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
}
