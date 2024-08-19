package TaskManagementSystem.dto.dSRequest;

import TaskManagementSystem.dto.dbo.GeneralTaskDBO;

public class TaskDSRequestModel extends GeneralTaskDBO {
    private String status;

    public TaskDSRequestModel(String title, String description, String priority, Integer authorId, Integer executorId, String comment, String status) {
        super(title, description, priority, authorId, executorId, comment);
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
